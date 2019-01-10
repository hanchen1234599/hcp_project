package share.service.container.base;

import java.util.concurrent.ConcurrentHashMap;
import com.hc.component.net.session.Session;
import io.netty.buffer.ByteBuf;
import share.service.container.ServiceContainerListener;
import share.service.container.ServiceContainerManager;
import share.service.container.ServiceContainerType;
import share.service.service.ServiceConnect;
import share.service.service.ServiceManager;

/**
 * 可肯能有一个server 或者 几个client
 * 
 * @author hanchen
 */
public class ServiceContainerManagerImpl implements ServiceContainerManager {
	private ServiceContainerListener listener = null;
	private int serviceContatinerID;
	private ConcurrentHashMap<Integer, ServiceManager> provider = new ConcurrentHashMap<>();// 本地服务容器
	
	private ConcurrentHashMap<String, ServiceConnect> serviceConnects = new ConcurrentHashMap<>();
	
	private ServiceContainerType serviceContainerType;

	public ServiceContainerManagerImpl(int serviceContatinerID, int port) {
		this.serviceContatinerID = serviceContatinerID;
		this.serviceContainerType = ServiceContainerType.SERVER;
	}

	public ServiceContainerManagerImpl(int serviceContatinerID) {
		this.serviceContatinerID = serviceContatinerID;
		this.serviceContainerType = ServiceContainerType.CLIENTS;
	}

	public void connectServiceContianer(String remoteIp, int remotePort) throws Exception {
		if (this.serviceContainerType == ServiceContainerType.SERVER) {
			throw new Exception("容器启动错误");
		}
	}

	@Override
	public void registListener(ServiceContainerListener listener) {
		this.listener = listener;
	}

	@Override
	public void open() throws Exception {
		
	}

	@Override
	public void close() {

	}

	@Override
	public ServiceContainerType getServiceContainerType() {
		return this.serviceContainerType;
	}

	/**
	 * 下边是容器的基本功能
	 */

	/**
	 * 注册服务(service)
	 */
	@Override
	public void registerService( ServiceManager manager) {
		this.provider.put(manager.getServiceId(), manager);
	}

	@Override
	public void remoteService(ServiceManager manager) {
		this.provider.remove(manager.getServiceId());
	}
	
	/**
	 * 容器收到数据
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
}

class ServerConfig {

}
