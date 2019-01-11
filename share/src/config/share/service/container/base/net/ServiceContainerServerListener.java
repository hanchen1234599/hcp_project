package share.service.container.base.net;

import java.util.concurrent.ConcurrentHashMap;

import com.google.protobuf.InvalidProtocolBufferException;
import com.hc.component.net.server.ServerListener;
import com.hc.component.net.server.ServerManager;
import com.hc.component.net.session.Session;
import com.hc.share.util.Trace;
import com.hc.share.util.Utils;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import share.service.container.ServiceContainerManager;

public class ServiceContainerServerListener implements ServerListener {
	private ServiceContainerManager serviceContainerManager = null;
	private ConcurrentHashMap<Session, ServiceContainerConnectCheck> checkSession = new ConcurrentHashMap<>();
	private ConcurrentHashMap<Session, Boolean> securitySession = new ConcurrentHashMap<>();
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
		String randomStr = Utils.randomString();
		checkSession.put(session, new ServiceContainerConnectCheck(randomStr, session));
		session.send(Unpooled.wrappedBuffer(hc.share.ProtoShare.ServiceConnectCheckReq.newBuilder().setKey(randomStr).build().toByteArray()));
	}

	@Override
	public void onRemoveSession(Session session) {
		Trace.logger.info("断开service container连接 sessionID："+ session.getSessionID() + " desc：" + session.getChannel());
		this.checkSession.remove(session);
		this.securitySession.remove(session);
	}

	@Override
	public void onData(Session session, ByteBuf body) {
		if(isSecurity(session)) {
			this.serviceContainerManager.onServiceContainerMessage(session, body);
		}else {
			//安全连接验证
			ServiceContainerConnectCheck check = this.checkSession.get(session);
			if(check == null) {
				session.getChannel().close();
			}else {
				try {
					String certificateStr = hc.share.ProtoShare.ServiceConnectCheckRsp.newBuilder().mergeFrom(body.array()).build().getCertificateStr();
					try {
						if(Utils.encodeMd5TwoBase64(check.getKey() + this.serviceContainerManager.getCertificateKey()).equals(certificateStr)) {
							this.securitySession.put(session, true);
						}
					} catch (Exception e) {
						Trace.logger.info(e);
					}
				} catch (InvalidProtocolBufferException e) {
					e.printStackTrace();
				}
			}
 		}
	}

	@Override
	public void OnExceptionCaught(Session session, Throwable cause) {
		Trace.logger.error("servicecontainer网络异常：sessionID:" + session.getSessionID() + "desc:" +session);
		Trace.logger.error(cause);
	}
	
	private boolean isSecurity( Session session ) {
		return this.securitySession.get(session) == true;
	}
}
class ServiceContainerConnectCheck{
	private String key;
	private Session session;
	public String getKey() {
		return key;
	}
	public Session getSession() {
		return session;
	}
	public ServiceContainerConnectCheck(String key, Session session) {
		this.key = key;
		this.session = session;
	}
}

