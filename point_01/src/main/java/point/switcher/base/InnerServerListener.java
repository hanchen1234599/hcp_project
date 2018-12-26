package point.switcher.base;

import io.netty.buffer.ByteBuf;
import point.switcher.GateApp;

import java.util.concurrent.ConcurrentHashMap;

import com.google.protobuf.InvalidProtocolBufferException;
import com.hc.component.net.server.ServerListener;
import com.hc.component.net.server.ServerManager;
import com.hc.component.net.session.Session;
import com.hc.share.service.Center;
import com.hc.share.service.Data;
import com.hc.share.service.Scene;
import com.hc.share.service.Server;
import com.hc.share.service.ServerType;
import com.hc.share.util.ProtoHelper;
import com.hc.share.util.Trace;

import hc.head.ProtoHead.Head.ProtoType;
/**
 * @author hanchen
 */
public class InnerServerListener implements ServerListener {
	private ConcurrentHashMap<Session, Server> servers = new ConcurrentHashMap<>();
	//连接缓存建立连接成功未认证服务器
	private ConcurrentHashMap<Session, Boolean> sessionCatch = new ConcurrentHashMap<>();

	@Override
	public void onInit(ServerManager manager) {
		GateApp.getInstace().setInnerManager(manager);
		try {
			manager.open();
		} catch (Exception e) {
			Trace.logger.info("Inner Server Component Open Error");
			e.printStackTrace();
		}
	}

	@Override
	public void onDestory(ServerManager manager) {
		GateApp.getInstace().setInnerManager(manager);
		Trace.logger.info("Inner Server Component Close");
	}

	@Override
	public void onAddSession(Session session) {
		sessionCatch.put(session, true);
	}

	@Override
	public void onRemoveSession(Session session) {
		sessionCatch.remove(session);
		Server server = servers.remove(session);
		if( server != null )
			GateApp.getInstace().onRemoveServer(server.getServerId());
	}

	@Override
	public void onExceptionSession(Session session) {
		session.getChannel().close();
	}

	@Override
	public void onData(Session session, ByteBuf buf) {
		if(sessionCatch.get(session) != null) {
			ProtoHelper.recvProtoBufByteBuf(buf, (result, srcID, desID, protoType, protoID, body)->{
				if(result) {
					if(protoType == ProtoType.PROTOBUF) {
						if(protoID == 21) {
							int bodyLen = body.readableBytes();
							byte[] bodyBuff = new byte[bodyLen];
							body.getBytes(0, bodyBuff, 0, bodyLen);
							try {
								hc.gate.S2GConnect.S2GReq req = hc.gate.S2GConnect.S2GReq.newBuilder().mergeFrom(bodyBuff).build();
								int serverID = req.getServerID();
								//这里要检查serverID的有效性
								if(serverID < 0 ) {
									sessionCatch.remove(session);
									session.getChannel().close();
									return;
								}
								int serverType = req.getServerType();
								Server server = null;
								if(serverType == ServerType.CENTER.ordinal()) {
									server = new Center(session, serverID);
								}else if(serverType == ServerType.DATA.ordinal()) {
									server = new Data(session, serverID);
								}else if(serverType == ServerType.SCENE.ordinal()) {
									server = new Scene(session, serverID);
								}else {
									sessionCatch.remove(session);
									session.getChannel().close();
									return;
								}
								sessionCatch.remove(session);
								servers.put(session, server);
								GateApp.getInstace().onAddServer(serverID, server);
								hc.gate.S2GConnect.S2GRsp.Builder rspBody = hc.gate.S2GConnect.S2GRsp.newBuilder();
								rspBody.setResult(true);
								byte[] rspBodyBuff = rspBody.build().toByteArray();
								session.send(ProtoHelper.createProtoBufByteBuf(GateApp.getInstace().getCurServiceID(), srcID, 22, rspBodyBuff));
							} catch (InvalidProtocolBufferException e) {
								e.printStackTrace();
								session.getChannel().close();
							}
						}else {
							session.getChannel().close();
						}
					}else {
						session.getChannel().close();
					}
				}
			});
		}else{
			Server server = servers.get(session);
			if( server == null ) {
				session.getChannel().close();
				return;
			}else {
				GateApp.getInstace().recvServerProto( server, buf );
			}
		}
	}
}
