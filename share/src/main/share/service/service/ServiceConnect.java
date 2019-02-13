package share.service.service;

/**
 * 这是一个很重要的类
 * 
 * @author hanchen
 */
public interface ServiceConnect {
	int getServiceID();
	int getServiceContainerID();
	String getRemoteServiceType();
	void send(int sourceServiceContainerId, int sourceServiceId, int protocolID, byte[] bytes);
}
