package share.service.service.base;

import share.service.container.ServiceContainerManager;
import share.service.service.Service;
import share.service.service.ServiceManager;

public class ServiceManagerImpl implements ServiceManager {
	private ServiceContainerManager container;
	private String serviceType;
	private Service service;
	private int serviceID;
	
	public ServiceManagerImpl( ServiceContainerManager container, int serviceID ) {
		this.container = container;
		this.serviceID = serviceID;
	}
	
	@Override
	public void registListener(Service listener) {
		this.service = listener;
	}

	@Override
	public void open() throws Exception {
		this.container.registerService(this);
	}

	@Override
	public void close() {
		this.container.remoteService(this);
	}

	@Override
	public void setServiceType(String serviceType) {
		this.serviceType = serviceType;
	}

	@Override
	public int getServiceId() {
		return this.serviceID;
	}

	@Override
	public void watchServiceType(String serviceType) {
		
	}
	
	@Override
	public boolean getIsWatch(String serviceType) {
		return false;
	}

	@Override
	public Service getService() {
		return this.service;
	}

	@Override
	public String getServiceType() {
		return this.serviceType;
	}
	
}
