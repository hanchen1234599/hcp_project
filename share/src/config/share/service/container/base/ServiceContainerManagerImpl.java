package share.service.container.base;

import java.util.Map.Entry;
import io.netty.buffer.ByteBuf;
import share.service.service.ServiceConnect;
import share.service.service.ServiceManager;
import share.server.config.base.ClientConfig;
import share.server.config.base.ServerConfig;
import com.hc.component.net.session.Session;
import java.util.concurrent.ConcurrentHashMap;
import com.hc.component.net.client.ClientComponent;
import com.hc.component.net.server.ServerComponent;
import share.service.container.ServiceContainerType;
import share.service.container.ServiceContainerListener;
import share.service.container.ServiceContainerManager;

/**
 * 可肯能有一个server 或者 几个client 容器为客户端类型 上报服务添加删除信息 接收服务添加删除信息 client 不提供服务注册功能
 * 
 * @author hanchen
 */
public class ServiceContainerManagerImpl implements ServiceContainerManager {

	private int serviceContatinerID;
	private String certificateKey = "123";
	@SuppressWarnings("unused")
	private ServiceContainerListener listener = null;
	private ServiceContainerType serviceContainerType;
	private ConcurrentHashMap<Integer, ServiceManager> provider = new ConcurrentHashMap<>();// 该容器内提供的服务
	private ConcurrentHashMap<Integer, ConcurrentHashMap<Integer, ServiceConnect>> serviceConnects = new ConcurrentHashMap<>();; // 在server

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
	 * 下边是容器的基本功能
	 */

	/**
	 * 添加服务管理器（区别于服务 在容器中的服务以serviceconnect的形式表现）
	 */
	@Override
	public void addServiceManager(ServiceManager manager) {
		noticeNewServiceExistServiceConnect(manager);
		this.provider.put(manager.getServiceId(), manager);
	}

	@Override
	public void deleteServiceManager(ServiceManager manager) {
		this.provider.remove(manager.getServiceId());
	}

	/**
	 * 上报服务的 添加 删除 server上报于自身 client上报于依赖容器
	 */
	public void reportServiceConnect(boolean isDelete, ServiceConnect conn) {
		if (this.serviceContainerType == ServiceContainerType.SERVER) {
			recvServiceConnect(isDelete, conn);
			return;
		} else {
			// 上报于依赖容器

		}
	}

	/**
	 * server 的功能 接收服务的 添加 删除消息
	 */
	public void recvServiceConnect(boolean isDelete, ServiceConnect conn) {
		// 通知server容器 服务数据发生改变
		dealLocalServiceConnect(isDelete, conn);
		// 通知client容器 服务数据发生改变
	}

	/**
	 * server client 处理Service改变数据
	 */
	public void dealLocalServiceConnect(boolean isDelete, ServiceConnect conn) {
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
	public void onServiceContainerMessage(Session session, ByteBuf body) {

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
		if (this.serviceContainerType == ServiceContainerType.SERVER) {
			// 这里启动服务端
			ServerConfig config = new ServerConfig();
			config.setBoosThreadNum(share.Config.Component.serverDefaultBoosThreadNum);
			config.setWorkeThreadNum(share.Config.Component.serverDefaultWorkeThreadNum);
			config.setInProtoLength(share.Config.Component.netProtoLength);
			config.setOutProtoLength(share.Config.Component.netProtoLength);
			config.setListener("share.service.container.base.net.ServiceContainerServerListener");
			ServerComponent component = config.createServerComponent(5001);
			component.build();
		} else if (this.serviceContainerType == ServiceContainerType.CLIENTS) {
			// 这里启动客户端 支持多个客户端
			ClientConfig config = new ClientConfig();
			config.setWorkeThreadNum(share.Config.Component.clientDefaultWorkeThreadNum);
			config.setOutProtoLength(share.Config.Component.netProtoLength);
			config.setInProtoLength(share.Config.Component.netProtoLength);
			config.setListener("share.service.container.base.net.ServiceContainerClientListener");
			ClientComponent component = config.createClientComponent("127.0.0.1", 5001);
			component.build();
		}
	}

	@Override
	public void close() {
		// 断开 网络连接 通知逻辑层

	}

	public String getCertificateKey() {
		return certificateKey;
	}

	public void setCertificateKey(String certificateKey) {
		this.certificateKey = certificateKey;
	}
}
