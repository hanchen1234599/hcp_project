package share.service.service;

public interface ServiceConnect {
	int getServiceID();
	int getServiceContainerID();
	String getRemoteServiceType();
	void send( byte[] bytes );
}
