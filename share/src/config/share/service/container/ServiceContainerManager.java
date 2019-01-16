package share.service.container;

import com.hc.component.net.session.Session;
import com.hc.share.Manager;

import io.netty.buffer.ByteBuf;
import share.service.service.ServiceManager;

public interface ServiceContainerManager extends Manager<ServiceContainerListener> {
	int getServiceContainerId();

	ServiceContainerType getServiceContainerType();

	void addServiceManager(ServiceManager service);

	void deleteServiceManager(ServiceManager service);

	void onServiceContainerMessage(Session session, ByteBuf body);

	String getCertificateKey();

	void setCertificateKey(String certificateKey);

	ServiceContainerListener getListener();

	void addSecuritySession(Session session, Integer serviceContainerID);
	
	void removeSecuritySession(Session session);

	boolean isSecuritySession(Session session);

	void setServerSession(Session serverSession);
	
	void openServerServiceConatiner(int port);
	
	void insertClientServiceConatiner(String remoteIp, int port);
}
