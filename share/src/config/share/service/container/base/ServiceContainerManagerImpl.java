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
	private ObjectMapper jsonParse = new ObjectMapper();
	private int serviceContatinerID;
	private String certificateKey = "123";
	private ServiceContainerListener listener = null;
	private ServiceContainerType serviceContainerType;
	private ConcurrentHashMap<Session, Integer> sessions = new ConcurrentHashMap<>();
	private Session serverSession = null; // clientSession 这里边才有值
	private ConcurrentHashMap<Integer, ServiceManager> provider = new ConcurrentHashMap<>();// 该容器内提供的服务
	private ConcurrentHashMap<Integer, ConcurrentHashMap<Integer, ServiceConnect>> serviceConnects = new ConcurrentHashMap<>(); // 所有的service存放到这里
	private ExecutorService exec = Executors.newSingleThreadExecutor();// 容器的处理线程

	public ServiceContainerManagerImpl(int serviceContatinerID, int port) {
		this.serviceContatinerID = serviceContatinerID;
		this.serviceContainerType = ServiceContainerType.SERVER;
	}

	public ServiceContainerManagerImpl(int serviceContatinerID) {
		this.serviceContatinerID = serviceContatinerID;
		this.serviceContainerType = ServiceContainerType.CLIENTS;
	}

	@Override
	public void registListener(ServiceContainerListener listener) {
		this.listener = listener;
	}

	/**
	 * 添加服务管理器（区别于服务 在容器中的服务以serviceconnect的形式表现）
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
	 * 刪除服务管理器（区别于服务 在容器中的服务以serviceconnect的形式表现）
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
	 * 上报服务的添加删除信息 server上报于自身 client上报于依赖容器
	 * 
	 * @throws JsonProcessingException
	 */
	public void reportServiceConnect(boolean isDelete, ServiceConnect conn) throws JsonProcessingException {
		if (this.serviceContainerType == ServiceContainerType.SERVER) {
			recvServiceConnect(isDelete, conn);
			return;
		} else {// 上报于依赖容器
			String json = jsonParse.writeValueAsString(conn);
			hc.share.ProtoShare.ReportServiceConnect.Builder builder = hc.share.ProtoShare.ReportServiceConnect
					.newBuilder();
			builder.setIsDelete(isDelete);
			builder.setConnectMsg(json);
			this.serverSession.send(ProtoHelper.createContainerProtoByteBuf(0, 0, 0, 0,
					ShareProtocol.reportServiceConnect, builder.build().toByteArray()));
		}
	}

	/**
	 * server功能 接收服务的添加删除消息
	 */
	public void recvServiceConnect(boolean isDelete, ServiceConnect conn) {
		if (this.serviceContainerType == ServiceContainerType.CLIENTS) {
			Trace.logger.info("容器功能发生异常--错误， 打死hc");
			return;
		}

		// 通知client容器 服务数据发生改变
		try {
			String json = jsonParse.writeValueAsString(conn);
			hc.share.ProtoShare.SvncServiceConnect.Builder builder = hc.share.ProtoShare.SvncServiceConnect
					.newBuilder();
			builder.setIsDelete(isDelete);
			builder.setConnectMsg(json);
			ByteBuf buff = ProtoHelper.createContainerProtoByteBuf(0, 0, 0, 0, ShareProtocol.svncServiceConnect,
					builder.build().toByteArray());
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
		} else {
			if (!this.serviceConnects.containsKey(conn.getServiceContainerID())) {
				this.serviceConnects.put(conn.getServiceContainerID(), new ConcurrentHashMap<>());
			}
			this.serviceConnects.get(conn.getServiceContainerID()).put(conn.getServiceID(), conn);
		}

		noticeOldServiceServiceConnectChange(isDelete, conn);
	}

	/**
	 * 已经存在服务添加删除(服务连接)通知
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
	 * 通知新添加的服务 已经存在的服务连接
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
		ProtoHelper.recvContainerProtoByteBuf(buf,
				(serviceContainID, serviceID, sourceServiceContainerID, sourceContainID, protoID, body) -> {
					if (this.serviceContainerType == ServiceContainerType.SERVER) {// server处理服务上报
						if (protoID == ShareProtocol.reportServiceConnect) {
							try {
								hc.share.ProtoShare.ReportServiceConnect rSConnect = hc.share.ProtoShare.ReportServiceConnect
										.newBuilder().mergeFrom(body.array()).build();
								boolean isDeleate = rSConnect.getIsDelete();
								ServiceConnectImpl conn = jsonParse.readValue(rSConnect.getConnectMsg(),
										ServiceConnectImpl.class);
								int msgContainerID = conn.getServiceContainerID();
								this.exec.execute(() -> {
									if (sessions.get(session) != msgContainerID)
										return;
									conn.setSession(session);
									recvServiceConnect(isDeleate, conn);
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
								hc.share.ProtoShare.SvncServiceConnect ssConnect = hc.share.ProtoShare.SvncServiceConnect
										.newBuilder().mergeFrom(body.array()).build();
								String connectMsg = ssConnect.getConnectMsg();
								boolean isDelete = ssConnect.getIsDelete();
								ServiceConnectImpl conn = jsonParse.readValue(connectMsg, ServiceConnectImpl.class);
								this.exec.submit(() -> {
									conn.setSession(session);
									dealContainerServiceConnect(isDelete, conn);
								});
							} catch (Exception e) {
								Trace.logger.info(e);
							}
							return;
						}
					}

				});
	}

	@Override
	public int getServiceContainerId() {
		return this.serviceContatinerID;
	}

	@Override
	public ServiceContainerType getServiceContainerType() {
		return this.serviceContainerType;
	}

	@Override
	public void open() throws Exception {
		this.exec.execute(() -> {
			Trace.logger.info("container open");
		});
	}

	@Override
	public void openServerServiceConatiner(int port) {
		this.exec.execute(() -> {
			ServerConfig config = new ServerConfig();
			config.setListener("share.service.container.base.net.ServiceContainerServerListener");
			try {
				ServerComponent component = config.createServerComponent(port);
				((ServiceContainerServerListener) component.getListener()).setServiceContainerManager(this);
				component.build();
			} catch (Exception e) {
				Trace.logger.error(e);
			}
		});
	}

	@Override
	public void insertClientServiceConatiner(String remoteIp, int port) {
		this.exec.execute(() -> {
			ClientConfig config = new ClientConfig();
			config.setListener("share.service.container.base.net.ServiceContainerClientListener");
			try {
				ClientComponent component = config.createClientComponent(remoteIp, port);
				((ServiceContainerClientListener) component.getListener()).setServiceContainerManager(this);
				component.build();
			} catch (Exception e) {
				Trace.logger.error(e);
			}
		});
	}

	@Override
	public void close() {
		this.exec.submit(() -> {
			for (Entry<Integer, ServiceManager> entry : this.provider.entrySet()) {
				entry.getValue().close();
			}
		});
		Runtime.getRuntime().exit(1);
	}

	@Override
	public ServiceContainerListener getListener() {
		return this.listener;
	}

	@Override
	public void addSecuritySession(Session session, Integer serviceContainerID) {
		this.exec.execute(() -> {
			this.sessions.put(session, serviceContainerID);
		});
	}

	@Override
	public void removeSecuritySession(Session session) {
		this.sessions.remove(session);
		this.exec.execute(() -> {
			if (this.serviceContainerType == ServiceContainerType.SERVER) {
				int remoteServiceContainerID = this.sessions.remove(session);
				ConcurrentHashMap<Integer, ServiceConnect> remoteServiceContainer = this.serviceConnects
						.get(remoteServiceContainerID);
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

	@Override
	public Session remoteSecuritySession(Session session) {
		return this.sessions.remove(session) == null ? null : session;
	}

	@Override
	public boolean isSecuritySession(Session session) {
		Integer serviceContainerID = this.sessions.get(session);
		return serviceContainerID != null && serviceContainerID != 0;
	}

	public Session getServerSession() {
		return serverSession;
	}

	public void setServerSession(Session serverSession) {
		this.serverSession = serverSession;
	}

	public String getCertificateKey() {
		return certificateKey;
	}

	public void setCertificateKey(String certificateKey) {
		this.certificateKey = certificateKey;
	}
}
