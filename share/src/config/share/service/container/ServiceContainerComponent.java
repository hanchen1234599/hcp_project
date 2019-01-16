package share.service.container;

import com.hc.share.Builder;
import com.hc.share.Component;

import share.service.container.base.ServiceContainerManagerImpl;

public class ServiceContainerComponent extends Component<ServiceContainerManager, ServiceContainerListener> implements Builder<ServiceContainerListener>{
	private int port = 0;
	private int serviceContainerID = 0;
	public ServiceContainerComponent(int serviceContainerID) {
		this.serviceContainerID = serviceContainerID;
	}
	public void setPort(int port) {
		this.port = port;
	}
	
	@Override
	public void build() throws Exception {
		this.manager = new ServiceContainerManagerImpl(this.serviceContainerID);
		if(this.port != 0)//服务器容器
			this.manager.openServerServiceConatiner(port);
		this.manager.registListener(this.listener);
		this.manager.getListener().onInit(this.manager);
	}
}
