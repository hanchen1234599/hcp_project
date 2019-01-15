package share.service.container;

import com.hc.component.net.session.Session;
import com.hc.share.Listener;

import share.service.service.Service;

public interface ServiceContainerListener extends Listener<ServiceContainerManager> {
	void onCreateSecurityConnect(Session session);
	void onDestorySecurityConnect(Session session);
	void onCreateService(Service session);
	void onDeleteService(Service session);
}
