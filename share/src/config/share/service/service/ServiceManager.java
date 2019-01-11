package share.service.service;

import com.hc.share.Manager;

public interface ServiceManager extends Manager<Service> {

	Service getService();

	void watchServiceType(String serviceType); // 关注的服务器类型

	void setServiceType(String serviceType); // 设置该服务的类型

	String getServiceType();

	int getServiceId();

	boolean getIsWatch(String serviceType);

	void noticeServiceConnect(boolean isDelete, ServiceConnect conn);
}
