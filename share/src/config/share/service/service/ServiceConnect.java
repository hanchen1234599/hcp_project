package share.service.service;

public interface ServiceConnect {
	String getServiceID();
	int getServiceContainerID();
	String getRomoteServiceType();
	void send( byte[] bytes );
}
