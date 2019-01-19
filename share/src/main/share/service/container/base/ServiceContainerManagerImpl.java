package share.service.container.base;

import java.util.Map.Entry;
import io.netty.buffer.ByteBuf;
import share.service.service.ServiceConnect;
import share.service.service.ServiceManager;
import share.service.service.base.ServiceConnectImpl;
import share.proto.config.ShareProtocol;
import share.proto.util.ProtoHelper;
import share.server.config.base.ClientConfig;
import share.server.config.base.ServerConfig;
import com.hc.component.net.session.Session;
import com.hc.share.util.Trace;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.protobuf.InvalidProtocolBufferException;
import com.hc.component.net.client.ClientComponent;
import com.hc.component.net.server.ServerComponent;
import share.service.container.ServiceContainerType;
import share.service.container.base.net.ServiceContainerClientListener;
import share.service.container.base.net.ServiceContainerServerListener;
import share.service.container.ServiceContainerListener;
import share.service.container.ServiceContainerManager;

/**
 * 可肯能有一个server 或者 几个client 容器为客户端类型 上报服务添加删除信息 接收服务添加删除信息 client 不提供服务注册功能
 * 
 * @author hanchen
 */
public class ServiceContainerManagerImpl implements ServiceContainerManager {

	private volatile int serviceContatinerID;
	private volatile String certificateKey = "123";
	private ConcurrentHashMap<Session, Integer> sessions = new ConcurrentHashMap<>();
	private ServiceContainerListener listener = null;
	private volatile ServiceContainerType serviceContainerType = null;
	private ConcurrentHashMap<Session, ConcurrentHashMap<Integer, Boolean>> serverSessions = new ConcurrentHashMap<>();
	private volatile int port = 0;

	private ObjectMapper jsonParse = new ObjectMapper();
	private ConcurrentHashMap<Integer, ServiceManager> provider = new ConcurrentHashMap<>();// 该容器内提供的服务
	private ConcurrentHashMap<Integer, ConcurrentHashMap<Integer, ServiceConnect>> serviceConnects = new ConcurrentHashMap<>(); // 所有的service存放到这里
	private ExecutorService exec = Executors.newSingleThreadExecutor();// 容器的处理线程

	public ServiceContainerManagerImpl(int serviceContatinerID) {
		this.serviceContatinerID = serviceContatinerID;
	}

	@Override
	public void registListener(ServiceContainerListener listener) {
		this.listener = listener;
	}

	/**
	 * exec 执行器执行 添加服务管理器（区别于服务 在容器中的服务以serviceconnect的形式表现）
	 */
	@Override
	public void addServiceManager(ServiceManager manager) {
		this.exec.execute(() -> {
			noticeNewServiceExistServiceConnect(manager);
			this.provider.put(manager.getServiceId(), manager);
			// 创建服务的remoteConnect
			ServiceConnectImpl connect = new ServiceConnectImpl();
			connect.setRemoteServiceType(manager.getServiceType());
			connect.setServiceContainerID(this.serviceContatinerID);
			connect.setServiceID(manager.getServiceId());
			try {
				reportServiceConnect(false, connect);
			} catch (JsonProcessingException e) {
				this.provider.remove(manager.getServiceId());
				Trace.logger.info(e);
			}
		});
	}

	/**
	 * exec 执行器执行 刪除服务管理器（区别于服务 在容器中的服务以serviceconnect的形式表现）
	 */
	@Override
	public void deleteServiceManager(ServiceManager manager) {
		this.exec.execute(() -> {
			this.provider.remove(manager.getServiceId());
			ServiceConnectImpl connect = new ServiceConnectImpl();
			connect.setRemoteServiceType(manager.getServiceType());
			connect.setServiceContainerID(this.serviceContatinerID);
			connect.setServiceID(manager.getServiceId());
			try {
				reportServiceConnect(true, connect);
			} catch (JsonProcessingException e) {
				this.provider.remove(manager.getServiceId());
				Trace.logger.info(e);
			}
		});
	}

	/**
	 * 上报服务的添加删除信息 server上报于自身 client上报于依赖容器 exec 执行器执行
	 * 
	 * @throws JsonProcessingException
	 */
	public void reportServiceConnect(boolean isDelete, ServiceConnect conn) throws JsonProcessingException {
		if (this.serviceContainerType == ServiceContainerType.SERVER) {
			recvServiceConnect(isDelete, conn);
			return;
		} else if (this.serviceContainerType == ServiceContainerType.CLIENTS) {// 上报于依赖容器
			String json = jsonParse.writeValueAsString(conn);
			hc.share.ProtoShare.ReportServiceConnect.Builder builder = hc.share.ProtoShare.ReportServiceConnect.newBuilder();
			builder.setIsDelete(isDelete);
			builder.setConnectMsg(json);
			for (Entry<Session, ConcurrentHashMap<Integer, Boolean>> entry : this.serverSessions.entrySet()) {
				if (entry.getValue().get(conn.getServiceID()) == null)
					entry.getKey().send(ProtoHelper.createContainerProtoByteBuf(0, 0, 0, 0, ShareProtocol.reportServiceConnect, builder.build().toByteArray()));
			}
		}
	}

	/**
	 * exec 执行器执行 server功能 接收服务的添加删除消息
	 */
	public void recvServiceConnect(boolean isDelete, ServiceConnect conn) {
		if (this.serviceContainerType == ServiceContainerType.CLIENTS) {
			Trace.logger.info("容器功能发生异常--错误， 打死hc");
			return;
		}

		// 通知client容器 服务数据发生改变
		try {
			String json = jsonParse.writeValueAsString(conn);
			hc.share.ProtoShare.SvncServiceConnect.Builder builder = hc.share.ProtoShare.SvncServiceConnect.newBuilder();
			builder.setIsDelete(isDelete);
			builder.setConnectMsg(json);
			ByteBuf buff = ProtoHelper.createContainerProtoByteBuf(0, 0, 0, 0, ShareProtocol.svncServiceConnect, builder.build().toByteArray());
			for (Entry<Session, Integer> entry : sessions.entrySet()) {
				entry.getKey().send(buff);
			}
		} catch (JsonProcessingException e) {
			Trace.logger.error(e);
			return;
		}

		// 容器处理服务数据发生改变
		dealContainerServiceConnect(isDelete, conn);
	}

	/**
	 * container处理Service改变数据
	 */
	public void dealContainerServiceConnect(boolean isDelete, ServiceConnect conn) {
		if (isDelete) {
			if (this.serviceConnects.containsKey(conn.getServiceContainerID())) {
				if (this.serviceConnects.get(conn.getServiceContainerID()).containsKey(conn.getServiceID())) {
					this.serviceConnects.get(conn.getServiceContainerID()).remove(conn.getServiceID());
				}
			}
			// this.listener.onCreateService(conn);
			this.listener.onDeleteService(conn);
		} else {
			if (!this.serviceConnects.containsKey(conn.getServiceContainerID())) {
				this.serviceConnects.put(conn.getServiceContainerID(), new ConcurrentHashMap<>());
			}
			this.serviceConnects.get(conn.getServiceContainerID()).put(conn.getServiceID(), conn);
			this.listener.onCreateService(conn);
		}

		noticeOldServiceServiceConnectChange(isDelete, conn);
	}

	/**
	 * exec 执行器执行 已经存在服务添加删除(服务连接)通知
	 */
	public void noticeOldServiceServiceConnectChange(boolean isDelete, ServiceConnect conn) {
		String serviceType = conn.getRemoteServiceType();
		for (Entry<Integer, ServiceManager> entry : this.provider.entrySet()) {
			if (entry.getValue().getIsWatch(serviceType)) {
				entry.getValue().noticeServiceConnect(isDelete, conn);
			}
		}
	}

	/**
	 * exec 执行器执行 通知新添加的服务 已经存在的服务连接
	 */
	public void noticeNewServiceExistServiceConnect(ServiceManager serviceManager) {
		for (Entry<Integer, ConcurrentHashMap<Integer, ServiceConnect>> mapEntry : this.serviceConnects.entrySet()) {
			for (Entry<Integer, ServiceConnect> entry : mapEntry.getValue().entrySet()) {
				if (serviceManager.getIsWatch(entry.getValue().getRemoteServiceType())) {
					serviceManager.getService().onNoticeServiceConnect(entry.getValue());
				}
			}
		}
	}

	/**
	 * 容器收到数据 通过数据判断 来源于那个Service
	 * 
	 * @param session
	 * @param body
	 */
	public void onServiceContainerMessage(Session session, ByteBuf buf) {
		ProtoHelper.recvContainerProtoByteBuf(buf, (serviceContainID, serviceID, sourceServiceContainerID, sourceContainID, protoID, body) -> {
			int length = body.readableBytes();
			byte[] buff = new byte[length];
			body.readBytes(buff);
			if (this.serviceContainerType == ServiceContainerType.SERVER) {// server处理服务上报
				if (protoID == ShareProtocol.reportServiceConnect) {
					try {
						hc.share.ProtoShare.ReportServiceConnect rSConnect = hc.share.ProtoShare.ReportServiceConnect.newBuilder().mergeFrom(buff).build();
						boolean isDeleate = rSConnect.getIsDelete();
						this.exec.execute(() -> {
							try {
								ServiceConnectImpl conn = jsonParse.readValue(rSConnect.getConnectMsg(), ServiceConnectImpl.class);
								int msgContainerID = conn.getServiceContainerID();
								if (sessions.get(session) != msgContainerID) {
									Trace.logger.info("containerID:" + sessions.get(session) + "service 配置错误");
									return;
								}
								conn.setSession(session);
								recvServiceConnect(isDeleate, conn);
							} catch (Exception e) {
								Trace.logger.error(e);
							}
						});
					} catch (Exception e) {
						Trace.logger.error(e);
					}
					return;
				}
			}
			if (this.serviceContainerType == ServiceContainerType.CLIENTS) {
				if (protoID == ShareProtocol.svncServiceConnect) {// client处理服务同数据
					try {
						hc.share.ProtoShare.SvncServiceConnect ssConnect = hc.share.ProtoShare.SvncServiceConnect.newBuilder().mergeFrom(buff).build();
						String connectMsg = ssConnect.getConnectMsg();
						boolean isDelete = ssConnect.getIsDelete();
						this.exec.submit(() -> {
							try {
								ServiceConnectImpl conn = jsonParse.readValue(connectMsg, ServiceConnectImpl.class);
								conn.setSession(session);
								Trace.logger.debug("收到服务器 containerID:" + conn.getServiceContainerID() + "serviceID:" + conn.getServiceID() + "同步服务的通知");
								dealContainerServiceConnect(isDelete, conn);
							} catch (Exception e) {
								Trace.logger.error(e);
							}
						});
					} catch (Exception e) {
						Trace.logger.info(e);
					}
					return;
				}else if(protoID == ShareProtocol.noticeServerConnectCreate) {
					this.exec.execute(()->{
						try {
							hc.share.ProtoShare.NoticeServerConnectCreate notice = hc.share.ProtoShare.NoticeServerConnectCreate.newBuilder().mergeFrom(buff).build();
							int noticeServiceContainerID = notice.getServiceContainID();
							Trace.logger.debug("收到服务器 containerID:" + noticeServiceContainerID + "连接检查成功的通知");
							this.serverSessions.put(session, new ConcurrentHashMap<>());
							for (Entry<Integer, ServiceManager> entry : this.provider.entrySet()) {
								ServiceConnectImpl connect = new ServiceConnectImpl();
								connect.setRemoteServiceType(entry.getValue().getServiceType());
								connect.setServiceContainerID(this.serviceContatinerID);
								connect.setServiceID(entry.getValue().getServiceId());
								try {
									reportServiceConnect(false, connect);
								} catch (JsonProcessingException e) {
									this.provider.remove(entry.getValue().getServiceId());
									Trace.logger.info(e);
								}
							}
						} catch (InvalidProtocolBufferException e) {
							Trace.logger.error(e);
						}
					});
				}
			}

		});
	}

	/**
	 * 线程安全
	 */
	@Override
	public int getServiceContainerId() {
		return this.serviceContatinerID;
	}

	/**
	 * 线程安全
	 */
	@Override
	public ServiceContainerType getServiceContainerType() {
		return this.serviceContainerType;
	}

	/**
	 * exec执行器
	 */
	@Override
	public void open() throws Exception {
		this.exec.execute(() -> {
			if (this.serviceContainerType == ServiceContainerType.SERVER) {
				ServerConfig config = new ServerConfig();
				config.setListener("share.service.container.base.net.ServiceContainerServerListener");
				try {
					ServerComponent component = config.createServerComponent(port);
					((ServiceContainerServerListener) component.getListener()).setServiceContainerManager(this);
					component.build();
				} catch (Exception e) {
					Trace.logger.error(e);
				}
			}
		});
	}

	/**
	 * 线程安全
	 */
	@Override
	public void openServerServiceConatiner(int port) {
		this.exec.execute(() -> {
			this.serviceContainerType = ServiceContainerType.SERVER;
			this.port = port;
		});
	}

	/**
	 * exec执行器
	 */
	@Override
	public void insertClientServiceConatiner(String remoteIp, int port) {
		this.exec.execute(() -> {
			if (this.serviceContainerType == ServiceContainerType.SERVER) {
				Trace.logger.error("不允许serverContainer添加客户单功能 ");
			} else {
				this.serviceContainerType = ServiceContainerType.CLIENTS;
				ClientConfig config = new ClientConfig();
				config.setListener("share.service.container.base.net.ServiceContainerClientListener");
				try {
					ClientComponent component = config.createClientComponent(remoteIp, port);
					((ServiceContainerClientListener) component.getListener()).setServiceContainerManager(this);
					component.build();
				} catch (Exception e) {
					Trace.logger.error(e);
				}
			}
		});
	}

	/**
	 * exec执行器
	 */
	@Override
	public void close() {
		this.exec.execute(() -> {
			for (Entry<Integer, ServiceManager> entry : this.provider.entrySet()) {
				entry.getValue().close();
			}
			Runtime.getRuntime().exit(1);
		});
	}

	/**
	 * 线程安全
	 */
	@Override
	public ServiceContainerListener getListener() {
		return this.listener;
	}

	/**
	 * 连接接受线程 exec执行器
	 */
	@Override
	public void addSecuritySession(Session session, Integer serviceContainerID) {
		if(this.serviceContainerType == ServiceContainerType.SERVER) {
			for(Entry<Session, Integer> entry: this.sessions.entrySet()) {
				if(entry.getValue() == serviceContainerID) {
					Trace.logger.error("serviceContainerID 重复");
					session.getChannel().close();
					break;
				}
			}
		}
		if(serviceContainerID == this.serviceContatinerID) {
			Trace.logger.error("serviceContainerID 重复");
			session.getChannel().close();
			return;
		}
		this.sessions.put(session, serviceContainerID); // 连接接受线程
		this.exec.execute(() -> {
			this.listener.onCreateSecurityConnect(session);
			if (this.serviceContainerType == ServiceContainerType.CLIENTS) { // 上报容器新开启的所有服务
				//client与server连接已经成功建立 服务器连接验证还未通过
				
			} else if (this.serviceContainerType == ServiceContainerType.SERVER) { // 通知当前开启的服务
				//通知服务器连接安全验证成功
				hc.share.ProtoShare.NoticeServerConnectCreate.Builder checkConnectbuilder = hc.share.ProtoShare.NoticeServerConnectCreate.newBuilder();
				checkConnectbuilder.setServiceContainID(this.serviceContatinerID);
				ByteBuf checkConnectbuff = ProtoHelper.createContainerProtoByteBuf(0, 0, 0, 0, ShareProtocol.noticeServerConnectCreate, checkConnectbuilder.build().toByteArray());
				session.send(checkConnectbuff);
				for (Entry<Integer, ConcurrentHashMap<Integer, ServiceConnect>> entryMap : this.serviceConnects.entrySet()) {
					for (Entry<Integer, ServiceConnect> entry : entryMap.getValue().entrySet()) {
						try {
							String json = jsonParse.writeValueAsString(entry.getValue());
							hc.share.ProtoShare.SvncServiceConnect.Builder builder = hc.share.ProtoShare.SvncServiceConnect.newBuilder();
							builder.setIsDelete(false);
							builder.setConnectMsg(json);
							ByteBuf buff = ProtoHelper.createContainerProtoByteBuf(0, 0, 0, 0, ShareProtocol.svncServiceConnect, builder.build().toByteArray());
							session.send(buff);
						} catch (JsonProcessingException e) {
							Trace.logger.error("服务器容器运行异常");
						}
					}
				}
			}
		});
	}

	/**
	 * 连接接受线程 exec执行器
	 */
	@Override
	public void removeSecuritySession(Session session) {
		Integer removeServiceContainerID = this.sessions.remove(session);
		if(removeServiceContainerID == null)
			return;
		this.exec.execute(() -> {
			if (this.serviceContainerType == ServiceContainerType.SERVER) {
				ConcurrentHashMap<Integer, ServiceConnect> remoteServiceContainer = this.serviceConnects.get(removeServiceContainerID);
				if (remoteServiceContainer != null) {
					for (Entry<Integer, ServiceConnect> entry : remoteServiceContainer.entrySet()) {
						recvServiceConnect(true, entry.getValue());
					}
				}
			} else if (this.serviceContainerType == ServiceContainerType.CLIENTS) {
				Trace.logger.error("依赖容器挂了");
				for (Entry<Integer, ServiceManager> entry : this.provider.entrySet()) {
					entry.getValue().close();
				}
			}
		});
	}

	/**
	 * 线程安全
	 */
	@Override
	public boolean isSecuritySession(Session session) {
		Integer serviceContainerID = this.sessions.get(session);
		return serviceContainerID != null && serviceContainerID != 0;
	}
	
	/**
	 * 线程安全
	 */
	public String getCertificateKey() {
		return certificateKey;
	}

	/**
	 * 线程安全
	 */
	public void setCertificateKey(String certificateKey) {
		this.certificateKey = certificateKey;
	}
}

//class ServerConainerConnect{
//	public ConcurrentHashMap<Integer, V>
//	
//}

