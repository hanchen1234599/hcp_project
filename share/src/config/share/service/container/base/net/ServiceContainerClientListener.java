package share.service.container.base.net;

import com.google.protobuf.InvalidProtocolBufferException;
import com.hc.component.net.client.ClientListener;
import com.hc.component.net.client.ClientManager;
import com.hc.component.net.session.Session;
import com.hc.share.util.Trace;
import com.hc.share.util.Utils;

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
		Trace.logger.info("连接container  sessionID:" + session.getSessionID() + " desc:" + session.getChannel());
	}

	@Override
	public void onUnConnect(Session session) {
		Trace.logger.info("断开container  sessionID:" + session.getSessionID() + " desc:" + session.getChannel());
		if (this.serviceContainerManager.remoteSecuritySession(session) != null) {
			this.serviceContainerManager.getListener().onDestorySecurityConnect(session);
		}
	}

	@Override
	public void onConnectException(Session session, Throwable cause) {
		Trace.logger.error("servicecontainer网络异常：sessionID:" + session.getSessionID() + "desc:" + session);
		Trace.logger.error(cause);
	}

	@Override
	public void onData(Session session, ByteBuf body) {
		if (this.serviceContainerManager.isSecuritySession(session)) {
			this.serviceContainerManager.onServiceContainerMessage(session, body);
		} else {
			// 这里进行安全验证
			try {
				hc.share.ProtoShare.ServiceConnectCheckReq connReq = hc.share.ProtoShare.ServiceConnectCheckReq
						.newBuilder().mergeFrom(body.array()).build();
				String key = connReq.getKey();
				Integer remoteServiceContainerID = connReq.getServiceContainerID();
				if(remoteServiceContainerID != null && remoteServiceContainerID != 0) {
					try {
						hc.share.ProtoShare.ServiceConnectCheckRsp.Builder connRspBuilder = hc.share.ProtoShare.ServiceConnectCheckRsp.newBuilder();
						connRspBuilder.setCertificateStr(Utils.encodeMd5TwoBase64(key + this.serviceContainerManager.getCertificateKey()));
						connRspBuilder.setServiceContainerID(this.serviceContainerManager.getServiceContainerId());
						session.send(Unpooled.wrappedBuffer(connRspBuilder.build().toByteArray()));
						this.serviceContainerManager.setServerSession(session);
						this.serviceContainerManager.addSecuritySession(session, remoteServiceContainerID);
						this.serviceContainerManager.getListener().onCreateSecurityConnect(session);
					} catch (Exception e) {
						Trace.logger.info(e);
						session.getChannel().close();
					}
				}	
			} catch (InvalidProtocolBufferException e) {
				Trace.logger.info(e);
				session.getChannel().close();
			}
		}
	}
}
