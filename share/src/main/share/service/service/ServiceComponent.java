package share.service.service;

import com.hc.share.Builder;
import com.hc.share.Component;
import share.service.container.ServiceContainerManager;
import share.service.service.base.ServiceManagerImpl;

public class ServiceComponent extends Component<ServiceManager, Service> implements Builder<Service> {
	private int serviceID = 0;
	private ServiceContainerManager container = null;
	public ServiceComponent(ServiceContainerManager container, int serviceID) {
		this.container = container;
		this.serviceID = serviceID;
	}

	@Override
	public void build() throws Exception {
		if(this.getListener() == null || serviceID <= 0 || container == null) {
			throw new Exception("服务创建失败");
		}
		this.manager = new ServiceManagerImpl(container, serviceID);
		this.manager.registListener(this.listener);
		this.listener.onInit(this.manager);
	}
}
