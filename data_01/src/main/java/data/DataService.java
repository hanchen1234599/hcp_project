package data;

import com.hc.share.util.Trace;

import io.netty.buffer.ByteBuf;
import share.service.service.Service;
import share.service.service.ServiceConnect;
import share.service.service.ServiceManager;

public class DataService implements Service  {
	ServiceManager manager = null;
	@Override
	public void onInit(ServiceManager manager) {
		this.manager = manager;
		try {
			this.manager.setServiceType("data");
			this.manager.watchServiceType("center");
			this.manager.open();
		} catch (Exception e) {
			Trace.logger.info(e);
		}
	}

	@Override
	public void onDestory(ServiceManager manager) {
		this.manager = null;
	}

	@Override
	public void onServiceMessage(ServiceConnect connect, ByteBuf body) {
		
	}

	@Override
	public void onNoticeServiceConnect(ServiceConnect conn) {
		Trace.logger.info("DataService onNoticeServiceConnect serviceContainerID:" + conn.getServiceContainerID() +" serviceID:" + conn.getServiceID() + " serviceType:" + conn.getRemoteServiceType());
	}

	@Override
	public void onNoticeServiceUnConnect(ServiceConnect conn) {
		Trace.logger.info("DataService onNoticeServiceConnect serviceContainerID:" + conn.getServiceContainerID() +" serviceID:" + conn.getServiceID() + " serviceType:" + conn.getRemoteServiceType());
	}

}
