package data;

import com.hc.component.net.session.Session;
import com.hc.share.util.Trace;

import share.service.container.ServiceContainerComponent;
import share.service.container.ServiceContainerListener;
import share.service.container.ServiceContainerManager;
import share.service.service.Service;
import share.service.service.ServiceComponent;
import share.service.service.ServiceConnect;

public class TestServiceContainerClient {
	public static void main(String args[]) {
		Trace.logger.info("ServiceContainer client ");
		// 启动一个容器
		ServiceContainerComponent containerComponent = new ServiceContainerComponent(1);
		try {
			containerComponent.setListener((ServiceContainerListener) Class.forName("data.TestClientContainer").newInstance());
		} catch (Exception e) {
			Trace.logger.error(e);
		}
		try {
			containerComponent.build();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

class TestClientContainer implements ServiceContainerListener {
	private ServiceContainerManager manager;

	@Override
	public void onInit(ServiceContainerManager manager) {
		this.manager = manager;
		try {
			this.manager.open();
		} catch (Exception e) {
			Trace.logger.info(e);
		}
		ServiceComponent serviceComponent = new ServiceComponent(this.manager, 3);
		try {
			serviceComponent.setListener((Service) Class.forName("data.DataService").newInstance());
			serviceComponent.build();
		} catch (Exception e) {
			Trace.logger.error(e);
		}
		this.manager.insertClientServiceConatiner("127.0.0.1", 5001);
	}

	@Override
	public void onDestory(ServiceContainerManager manager) {
		this.manager = null;
	}

	@Override
	public void onCreateSecurityConnect(Session session) {
		Trace.logger.info("TestContainerListener session connect sessionID:" + session.getSessionID());
	}

	@Override
	public void onDestorySecurityConnect(Session session) {
		Trace.logger.info("TestContainerListener onDestorySecurityConnect sessionID:" + session.getSessionID());	
	}

	@Override
	public void onCreateService(ServiceConnect conn) {
		Trace.logger.info("TestContainerListener onCreateService serviceContainerID:" + conn.getServiceContainerID() +" serviceID:" + conn.getServiceID() + " serviceType:" + conn.getRemoteServiceType());	
	}

	@Override
	public void onDeleteService(ServiceConnect conn) {
		Trace.logger.info("TestContainerListener onDeleteService serviceContainerID:" + conn.getServiceContainerID() +" serviceID:" + conn.getServiceID() + " serviceType:" + conn.getRemoteServiceType());
	}

}