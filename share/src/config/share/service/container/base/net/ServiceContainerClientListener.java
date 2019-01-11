package share.service.container.base.net;

import com.hc.component.net.client.ClientListener;
import com.hc.component.net.client.ClientManager;
import com.hc.component.net.session.Session;
import com.hc.share.util.Trace;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import share.service.container.ServiceContainerManager;

public class ServiceContainerClientListener implements ClientListener {
	private ServiceContainerManager serviceContainerManager = null;
	@SuppressWarnings("unused")
	private ClientManager manager;
	public void setServiceContainerManager(ServiceContainerManager serviceContainerManager) {
		this.serviceContainerManager = serviceContainerManager;
	}

	@Override
	public void onInit(ClientManager manager) {
		this.manager = manager;
	}

	@Override
	public void onDestory(ClientManager manager) {
		this.manager = null;
	}

	@Override
	public void onConnect(Session session) {
		Trace.logger.info("连接container  sessionID:" + session.getSessionID() +" desc:" + session.getChannel());
		//发起安全连接验证
		byte[] body = hc.share.ProtoShare.ServiceConnectCheckReq.newBuilder().setCertificateStr(this.serviceContainerManager.getCertificateKey()).build().toByteArray();
		session.send(Unpooled.wrappedBuffer(body));
	}

	@Override
	public void onUnConnect(Session session) {
		Trace.logger.info("断开container  sessionID:" + session.getSessionID() +" desc:" + session.getChannel());
	}

	@Override
	public void onConnectException(Session session, Throwable cause) {
		Trace.logger.error("servicecontainer网络异常：sessionID:" + session.getSessionID() + "desc:" +session);
		Trace.logger.error(cause);
	}

	@Override
	public void onData(Session session, ByteBuf body) {
		if(isSecurity(session)) {
			this.serviceContainerManager.onServiceContainerMessage(session, body);
		}else {
			//这里进行安全验证
		}
	}
	
	private boolean isSecurity( Session session ) {
		return true;
	}
}
