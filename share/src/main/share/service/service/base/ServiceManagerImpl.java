package share.service.service.base;

import java.util.HashMap;

import com.hc.share.util.Trace;

import share.service.container.ServiceContainerManager;
import share.service.service.Service;
import share.service.service.ServiceConnect;
import share.service.service.ServiceManager;

public class ServiceManagerImpl implements ServiceManager {
	private ServiceContainerManager container;
	private String serviceType;
	private Service service;
	private int serviceID;
	private volatile boolean isOpen = false;
	private HashMap<String, Boolean> watchServiceType = new HashMap<>();

	public ServiceManagerImpl(ServiceContainerManager container, int serviceID) {
		this.container = container;
		this.serviceID = serviceID;
	}

	@Override
	public void noticeServiceConnect(boolean isDelete, ServiceConnect conn) {
		if (isDelete) {
			this.service.onNoticeServiceUnConnect(conn);
		} else {
			this.service.onNoticeServiceConnect(conn);
		}
	}

	@Override
	public Service getService() {
		return this.service;
	}

	@Override
	public String getServiceType() {
		return this.serviceType;
	}

	@Override
	public void registListener(Service listener) {
		this.service = listener;
	}

	@Override
	public void open() throws Exception {
		this.container.addServiceManager(this);
		this.isOpen = true;
	}

	@Override
	public void close() {
		this.container.deleteServiceManager(this);
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
		if(isOpen == true) {
			Trace.logger.error("service open after watch service");
			return;
		}
		this.watchServiceType.put(serviceType, true);
	}

	@Override
	public boolean getIsWatch(String serviceType) {
		return this.watchServiceType.get(serviceType) != null ? this.watchServiceType.get(serviceType) == true : false;
	}
}
