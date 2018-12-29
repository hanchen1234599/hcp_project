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
	}

	@Override
	public void onAddSession(Session session) {
		Trace.logger.info("内网建立连接  sessionID:" + session.getSessionID());
	}

	@Override
	public void onRemoveSession(Session session) {
		Server server = servers.remove(session);
		if( server != null )
			GateApp.getInstace().onRemoveServer(server);
	}

	@Override
	public void onData(Session session, ByteBuf buf) {
		Server server = servers.get(session);
		if(server == null) {
			ProtoHelper.recvProtoBufByteBuf(buf, (result, srcID, desID, protoType, protoID, body)->{
				if(result) {
					if(protoType == ProtoType.PROTOBUF) {
						if(protoID == hc.protoconfig.GateProtocol.S2GReq) {
							int bodyLen = body.readableBytes();
							byte[] bodyBuff = new byte[bodyLen];
							body.getBytes(0, bodyBuff, 0, bodyLen);
							try {
								hc.gate.S2GConnect.S2GReq req = hc.gate.S2GConnect.S2GReq.newBuilder().mergeFrom(bodyBuff).build();
								int serverID = req.getServerID();
								//这里要检查serverID的有效性
								if(serverID < 0 ) {
									session.getChannel().close();
									return;
								}
								int serverType = req.getServerType();
								Server newServer = null;
								if(serverType == ServerType.CENTER.ordinal()) {
									newServer = new Center(session, serverID);
								}else if(serverType == ServerType.DATA.ordinal()) {
									newServer = new Data(session, serverID);
								}else if(serverType == ServerType.SCENE.ordinal()) {
									newServer = new Scene(session, serverID);
								}else {
									session.getChannel().close();
									return;
								}
								servers.put(session, newServer);
								GateApp.getInstace().onAddServer(serverID, newServer);
								hc.gate.S2GConnect.S2GRsp.Builder rspBody = hc.gate.S2GConnect.S2GRsp.newBuilder();
								rspBody.setResult(true);
								byte[] rspBodyBuff = rspBody.build().toByteArray();
								session.send(ProtoHelper.createProtoBufByteBuf(GateApp.getInstace().getCurServiceID(), srcID, hc.protoconfig.GateProtocol.S2GRsp, rspBodyBuff));
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
			GateApp.getInstace().recvServerProto( server, buf );
		}
	}

	@Override
	public void OnExceptionCaught(Session session, Throwable cause) {
		Trace.logger.info(cause);
	}
}
