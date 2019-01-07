package gate.base;

import com.hc.component.net.client.ClientListener;
import com.hc.component.net.client.ClientManager;
import com.hc.component.net.session.Session;
import com.hc.share.util.Trace;

import gate.GateApp;
import hc.head.ProtoHead.Head.ProtoType;
import io.netty.buffer.ByteBuf;
import share.proto.config.CenterProtocol;
import share.proto.util.ProtoHelper;
import share.server.service.Center;
import share.server.service.ServerType;

public class Gate2CenterListener implements ClientListener {
	private ClientManager manager = null;
	private ConnectCenter connectCenter =null;
	private volatile boolean state = false;
	@Override
	public void onInit(ClientManager manager) {
		this.manager = manager;
		try {
			this.manager.open();
		} catch (Exception e) {
			Trace.logger.info(e);
			Runtime.getRuntime().exit(1);
		}
	}

	@Override
	public void onDestory(ClientManager manager) {
		this.manager = null;
	}

	@Override
	public void onConnect(Session session) {
		connectCenter = new ConnectCenter();
	}

	@Override
	public void onUnConnect(Session session) {
		connectCenter = null;
		Trace.logger.info("gate 与  center 的链接被断开");
	}

	@Override
	public void onData(Session session, ByteBuf body) {
		if(this.connectCenter != null) {
			this.connectCenter.tryCreate(session, body, this);
		}else {
			if(this.state == false) {
				Trace.logger.info("gate 与  center 的链接未连接成功");
			}else {
				
			}
		}
	}

	@Override
	public void onConnectException(Session session, Throwable cause) {
		Trace.logger.info("与中心通讯发生异常 ");
		Trace.logger.error(cause);
	}
	
	public void createCenterSuccess() {
		this.connectCenter = null;
		this.state = true;
	}
}
class ConnectCenter{
	static enum CreateState{ CREATECONNECT, CHECKCONNECT, RECVCONNECTMESSAGE, READY }
	private CreateState state = CreateState.CREATECONNECT;
	public void tryCreate( Session session, ByteBuf buf, Gate2CenterListener gate2CenterListener ) {
		if(this.state == CreateState.CREATECONNECT) {
			ProtoHelper.recvProtoBufByteBuf(buf, (result, srcID, desID, protoType, protoID, body)->{
				if (protoType == ProtoType.PROTOBUF) {
					if(result) {
						if(protoID == CenterProtocol.CenterCreateServerConnectReq) {
							GateApp.getInstance().getServerInnerLogic().recvMessage2Protocol(protoID, body, (recvMsg)->{
								hc.center.CenterProto.CenterCreateServerConnectReq msg = (hc.center.CenterProto.CenterCreateServerConnectReq)recvMsg;
								String value = msg.getMsg();
								if(value.equals("hello")) {
									hc.center.CenterProto.CenterCreateServerConnectRsp.Builder builder = (hc.center.CenterProto.CenterCreateServerConnectRsp.Builder) GateApp.getInstance().getServerInnerLogic().getBuilder(CenterProtocol.CenterCreateServerConnectRsp);
									builder.setMsg("ok");
									session.getChannel().writeAndFlush(ProtoHelper.createProtoBufByteBuf(GateApp.getInstance().getServerID(), 0, CenterProtocol.CenterCreateServerConnectRsp, builder.build().toByteArray()));
									this.state = CreateState.CHECKCONNECT;
								}else {
									session.getChannel().close();
								}
							});
						}else {
							session.getChannel().close();
						}
					}else {
						Trace.logger.info("协议解析错误");
					}
				}else {
					Trace.logger.info("暂时不支持其他类型的协议");
				}
			});
		}else if( this.state == CreateState.CHECKCONNECT ) {
			ProtoHelper.recvProtoBufByteBuf(buf, (result, srcID, desID, protoType, protoID, body)->{
				if (protoType == ProtoType.PROTOBUF) {
					if(result) {
						if(protoID == CenterProtocol.CenterServerCheckReq) {
							GateApp.getInstance().getServerInnerLogic().recvMessage2Protocol(protoID, body, (recvMsg)->{
								hc.center.CenterProto.CenterServerCheckReq msg = (hc.center.CenterProto.CenterServerCheckReq)recvMsg;
								String value = msg.getCheck();
								if(value.equals("123")) {
									this.state = CreateState.RECVCONNECTMESSAGE;
									hc.center.CenterProto.CenterServerCheckRsp.Builder builder = (hc.center.CenterProto.CenterServerCheckRsp.Builder) GateApp.getInstance().getServerInnerLogic().getBuilder(CenterProtocol.CenterServerCheckRsp);
									builder.setCheck("223");
									session.getChannel().writeAndFlush(ProtoHelper.createProtoBufByteBuf(GateApp.getInstance().getServerID(), 0, CenterProtocol.CenterServerCheckRsp, builder.build().toByteArray()));
								}else {
									session.getChannel().close();
								}
							});
						}else {
							session.getChannel().close();
						}
					}else {
						Trace.logger.info("协议解析错误");
					}
				}else {
					Trace.logger.info("暂时不支持其他类型的协议");
				}
			});
		}else if( this.state == CreateState.RECVCONNECTMESSAGE ) {
			ProtoHelper.recvProtoBufByteBuf(buf, (result, srcID, desID, protoType, protoID, body)->{
				if (protoType == ProtoType.PROTOBUF) {
					if(result) {
						if(protoID == CenterProtocol.CenterServerMessageAskReq) {
							GateApp.getInstance().getServerInnerLogic().recvMessage2Protocol(protoID, body, (recvMsg)->{
								hc.center.CenterProto.CenterServerMessageAskReq msg = (hc.center.CenterProto.CenterServerMessageAskReq)recvMsg;
								int centerServerID = msg.getServerID();
								String centerServerName = msg.getServerName();
								if(msg.getMsg().equals("ask")) {
									hc.center.CenterProto.CenterServerMessageAskRsp.Builder builder = (hc.center.CenterProto.CenterServerMessageAskRsp.Builder) GateApp.getInstance().getServerInnerLogic().getBuilder(CenterProtocol.CenterServerMessageAskRsp);
									builder.setServerID(GateApp.getInstance().getServerID());
									builder.setServerType(ServerType.GATE.ordinal());
									builder.setServerName(GateApp.getInstance().getServerName());
									builder.setJsonMsg(GateApp.getInstance().getServerMsg());
									session.getChannel().writeAndFlush(ProtoHelper.createProtoBufByteBuf(GateApp.getInstance().getServerID(), 0, CenterProtocol.CenterServerMessageAskRsp, builder.build().toByteArray()));
									gate2CenterListener.createCenterSuccess();
									Center center = new Center(session, centerServerID);
									center.setServerName(centerServerName);
									center.setOpen(true);
									GateApp.getInstance().onAddServer( centerServerID, center );
								}else {
									session.getChannel().close();
								}
							});
						}else {
							session.getChannel().close();
						}
					}else {
						Trace.logger.info("协议解析错误");
					}
				}else {
					Trace.logger.info("暂时不支持其他类型的协议");
				}
			});
		}
	}
}
