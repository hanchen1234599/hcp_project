package point.switcher.base;

import com.hc.component.net.server.ServerListener;
import com.hc.component.net.server.ServerManager;
import com.hc.component.net.session.Session;
import com.hc.share.util.ProtoHelper;
import com.hc.share.util.Trace;

import hc.head.ProtoHead.Head.ProtoType;
import io.netty.buffer.ByteBuf;
import point.switcher.GateApp;

/**
 * @author hanchen
 */
public class OutServerListener implements ServerListener {
	@Override
	public void onInit(ServerManager manager) {
		GateApp.getInstance().setOuterManager(manager);
		try {
			manager.open();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onDestory(ServerManager manager) {
		GateApp.getInstance().setOuterManager(null);
		Trace.logger.info("OutServerListener onDestory");
	}

	@Override
	public void onAddSession(Session session) {
		GateApp.getInstance().onClientInactive(session);
	}

	@Override
	public void onRemoveSession(Session session) {
		GateApp.getInstance().onClientUnactive(session);
	}

	@Override
	public void onData(Session session, ByteBuf buf) {
		if (session.getParamete() == null) {
			ProtoHelper.recvProtoBufByteBuf(buf, (result, srcID, desID, protoType, protoID, body) -> {
				if (result) {
					if (protoType == ProtoType.PROTOBUF && protoID == hc.config.proto.LoginProtocol.LoginReq) {
						int bodyLen = body.readableBytes();
						byte[] reqbody = new byte[bodyLen];
						body.getBytes(0, reqbody);
						try {
							hc.login.PessCheck.LoginReq loginReq = hc.login.PessCheck.LoginReq.newBuilder()
									.mergeFrom(reqbody).build();
							String roleName = loginReq.getAccountName();
							hc.login.PessCheck.LoginPessReq.Builder loginPassReqBuilder = hc.login.PessCheck.LoginPessReq
									.newBuilder();
							loginPassReqBuilder.setAccountName(roleName);
							loginPassReqBuilder.setSessionID(session.getSessionID());
							GateApp.getInstance().getLogin().getSession()
									.send(ProtoHelper.createProtoBufByteBuf(GateApp.getInstance().getServerID(), 0,
											hc.config.proto.LoginProtocol.LoginPessReq,
											loginPassReqBuilder.build().toByteArray()));
						} catch (Exception e) {
							Trace.logger.info(e);
						}
					} else {
						Trace.logger.warn("sessionID:" + session.getSessionID() + "protocol or protoID type error");
						session.getChannel().close();
					}
				} else {
					Trace.logger.info("client 登陆头协议解析错误");
					session.getChannel().close();
				}
			});
		} else {
			GateApp.getInstance().recvClientProto(session, buf);
		}
	}

	@Override
	public void OnExceptionCaught(Session session, Throwable cause) {
		session.getChannel().close();
		Trace.logger.info(cause);
	}
}
