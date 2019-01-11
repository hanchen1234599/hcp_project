package share.service.container.base.net;

import com.hc.component.net.server.ServerListener;
import com.hc.component.net.server.ServerManager;
import com.hc.component.net.session.Session;
import com.hc.share.util.Trace;

import io.netty.buffer.ByteBuf;
import share.service.container.ServiceContainerManager;

public class ServiceContainerServerListener implements ServerListener {
	private ServiceContainerManager serviceContainerManager = null;
	@SuppressWarnings("unused")
	private ServerManager manager;
	public void setServiceContainerManager(ServiceContainerManager serviceContainerManager) {
		this.serviceContainerManager = serviceContainerManager;
	}

	@Override
	public void onInit(ServerManager manager) {
		this.manager = manager;
	}

	@Override
	public void onDestory(ServerManager manager) {
		this.manager = null;
	}

	@Override
	public void onAddSession(Session session) {
		Trace.logger.info("收到service container连接  sessionID:" + session.getSessionID() +" desc:" + session.getChannel());
		// 安全连接请求
		
	}

	@Override
	public void onRemoveSession(Session session) {
		Trace.logger.info("断开service container连接 sessionID："+ session.getSessionID() + " desc：" + session.getChannel());
	}

	@Override
	public void onData(Session session, ByteBuf body) {
		if(isSecurity(session)) {
			this.serviceContainerManager.onServiceContainerMessage(session, body);
		}else {
			//安全连接验证
			
		}
	}

	@Override
	public void OnExceptionCaught(Session session, Throwable cause) {
		Trace.logger.error("servicecontainer网络异常：sessionID:" + session.getSessionID() + "desc:" +session);
		Trace.logger.error(cause);
	}
	
	private boolean isSecurity( Session session ) {
		return true;
	}
}
