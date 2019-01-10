package share.service.container;

import com.hc.share.Manager;
import share.service.service.ServiceManager;

public interface ServiceContainerManager extends Manager<ServiceContainerListener> {
	int getServiceContainerId();

	ServiceContainerType getServiceContainerType();

	void registerService( ServiceManager service );

	void remoteService(ServiceManager service);
}
