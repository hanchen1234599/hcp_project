package center;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import org.dom4j.Element;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hc.component.db.mysql.MysqlComponent;
import com.hc.component.db.mysql.MysqlListener;
import com.hc.component.db.mysql.MysqlManager;
import com.hc.component.net.server.ServerComponent;
import com.hc.component.net.server.ServerListener;
import com.hc.component.net.server.ServerManager;
import com.hc.component.net.session.Session;
import com.hc.share.util.Trace;
import com.hc.share.util.XmlReader;

import hc.head.ProtoHead.Head.ProtoType;
import io.netty.buffer.ByteBuf;
import share.proto.config.CenterProtocol;
import share.proto.config.base.ProtocolLogic;
import share.proto.util.ProtoHelper;
import share.server.config.MmoServerConfigTemp;
import share.server.service.Data;
import share.server.service.Gate;
import share.server.service.Scene;
import share.server.service.Server;
import share.server.service.ServerType;

public class CenterApp {
	private static CenterApp instance = new CenterApp();

	private CenterApp() {
	}

	public static CenterApp getInstance() {
		return instance;
	}

	private int serverID = 0;
	private String serverName = "";
	private MysqlManager dbManager = null;
	private ServerManager netManager = null;
	private ConcurrentHashMap<Integer, Server> servers = new ConcurrentHashMap<>(); // 服务器id对应 server
	private ConcurrentHashMap<Session, Server> session2server = new ConcurrentHashMap<>(); // 服务器id对应 server
	private ConcurrentHashMap<Session, CreateServer> tempSession = new ConcurrentHashMap<>(); // 服务器id对应 server
	private ProtocolLogic serverConnectLogic = new ProtocolLogic();

	public void OnAddServerConnect(Session session) {
		this.serverConnectLogic.exec(() -> {
			CreateServer create = this.tempSession.get(session);
			if (create == null) {
				create = new CreateServer();
				this.tempSession.put(session, create);
			}
			hc.center.CenterProto.CenterCreateServerConnectReq.Builder builder = (hc.center.CenterProto.CenterCreateServerConnectReq.Builder) this.serverConnectLogic
					.getBuilder(CenterProtocol.CenterCreateServerConnectReq);
			builder.setMsg("hello");
			session.getChannel().writeAndFlush(ProtoHelper.createProtoBufByteBuf(this.serverID, 0,
					CenterProtocol.CenterCreateServerConnectReq, builder.build().toByteArray()));
		});
	}

	public void OnRemoveServerConnect(Session session) {
		Trace.logger.info("断开连接sessionid:" + session.getSessionID());
		this.serverConnectLogic.exec(() -> {
			this.tempSession.remove(session);
		});    
	}

	public void OnServerConnectException(Session session, Throwable cause) {
		Trace.logger.info("异常连接  sessionid:" + session.getSessionID());
		Trace.logger.warn(cause);
	}

	public void OnServerData(Session session, ByteBuf body) {
		if (this.session2server.get(session) == null) {
			this.serverConnectLogic.exec(() -> {
				CreateServer create = this.tempSession.get(session);
				if (create == null) {
					return;
				}
				create.tryCreate(session, body);
			});
		} else {

		}
	}

	public ProtocolLogic getServerConnectLogic() {
		return this.serverConnectLogic;
	}

	public void addServer(Server server) {
		this.servers.put(server.getServerId(), server);
		this.session2server.put(server.getSession(), server);
	}

	public void removeServer(int serverID) {
		this.servers.remove(serverID);
	}

	public void close() {
		this.dbManager.close();
		this.netManager.close();
	}

	public void start() {
		try {
			this.init();
			this.serverConnectLogic.init();
			this.serverConnectLogic.setExec(Executors.newSingleThreadExecutor());
		} catch (Exception e) {
			Trace.logger.info(e);
			Runtime.getRuntime().exit(1);
		}
	}

	public void init() throws Exception {
		MmoServerConfigTemp.getInstace().init("../share/config/servertypeconfig.xml");
		Element centerRoot = XmlReader.getInstance().readFile("../share/config/serverconfig.xml").getRootElement();
		Element centerPoint = XmlReader.getElementByAttributeWithElementName(centerRoot, "point", "type", "center");
		Element centerDbConfig = XmlReader.getElementByAttribute(centerRoot.element("dbconfig"), "id", centerPoint.element("centerdb").attribute("id").getText());
		
		this.serverID = Integer.parseInt(centerPoint.attribute("serverid").getText());
		this.serverName = centerPoint.attribute("name").getText();
		MysqlComponent mysql = new MysqlComponent();
		mysql.setHikaricpConfigPaht(centerDbConfig.attribute("hikariconfig").getText());
		mysql.setListener((MysqlListener) Class.forName(MmoServerConfigTemp.getInstace().getMysqlConfig("center", "centerdb").getListener()).newInstance()); 
		mysql.setUseThread(MmoServerConfigTemp.getInstace().getMysqlConfig("center", "centerdb").getWorkeThreadNum());
		mysql.build(); // 数据库组件启动
		ServerComponent serverComponent = new ServerComponent();
		serverComponent.setEventLoop(MmoServerConfigTemp.getInstace().getServerConfig("center", "inner").getBoosThreadNum(), MmoServerConfigTemp.getInstace().getServerConfig("center", "inner").getWorkeThreadNum());
		serverComponent.setInProtoLength(MmoServerConfigTemp.getInstace().getServerConfig("center", "inner").getInProtoLength());
		serverComponent.setOutProtoLength(MmoServerConfigTemp.getInstace().getServerConfig("center", "inner").getOutProtoLength());
		serverComponent.setListener((ServerListener) Class.forName(MmoServerConfigTemp.getInstace().getServerConfig("center", "inner").getListener()).newInstance());
		serverComponent.setPort(Integer.parseInt(centerPoint.element("inner").attribute("port").getText()));
		serverComponent.build(); // 网络连接 启动
	}

	public void setDbManager(MysqlManager dbManager) {
		this.dbManager = dbManager;
	}

	public void setNetManager(ServerManager netManager) {
		this.netManager = netManager;
	}

	public int getServerID() {
		return serverID;
	}

	public String getServerName() {
		return serverName;
	}
}

class CreateServer {
	static enum CreateState {
		CREATECONNECT, CHECKCONNECT, RECVCONNECTMESSAGE, READY
	}

	private CreateState state = CreateState.CREATECONNECT;

	public void tryCreate(Session session, ByteBuf buf) {
		if (this.state == CreateState.CREATECONNECT) {
			ProtoHelper.recvProtoBufByteBuf(buf, (result, srcID, desID, protoType, protoID, body) -> {
				if (protoType == ProtoType.PROTOBUF) {
					if (result) {
						if (protoID == CenterProtocol.CenterCreateServerConnectRsp) {
							CenterApp.getInstance().getServerConnectLogic().recvMessage2Protocol(protoID, body,
									(recvMsg) -> {
										hc.center.CenterProto.CenterCreateServerConnectRsp msg = (hc.center.CenterProto.CenterCreateServerConnectRsp) recvMsg;
										String value = msg.getMsg();
										if (value.equals("ok")) {
											this.state = CreateState.CHECKCONNECT;
											hc.center.CenterProto.CenterServerCheckReq.Builder builder = (hc.center.CenterProto.CenterServerCheckReq.Builder) CenterApp
													.getInstance().getServerConnectLogic()
													.getBuilder(CenterProtocol.CenterServerCheckReq);
											builder.setCheck("123");
											session.getChannel()
													.writeAndFlush(ProtoHelper.createProtoBufByteBuf(
															CenterApp.getInstance().getServerID(), 0,
															CenterProtocol.CenterServerCheckReq,
															builder.build().toByteArray()));
										} else {
											session.getChannel().close();
										}
									});
						} else {
							session.getChannel().close();
						}
					} else {
						Trace.logger.info("协议解析错误");
					}
				} else {
					Trace.logger.info("暂时不支持其他类型的协议");
				}
			});
		} else if (this.state == CreateState.CHECKCONNECT) {
			ProtoHelper.recvProtoBufByteBuf(buf, (result, srcID, desID, protoType, protoID, body) -> {
				if (protoType == ProtoType.PROTOBUF) {
					if (result) {
						if (protoID == CenterProtocol.CenterServerCheckRsp) {
							CenterApp.getInstance().getServerConnectLogic().recvMessage2Protocol(protoID, body,
									(recvMsg) -> {
										hc.center.CenterProto.CenterServerCheckRsp msg = (hc.center.CenterProto.CenterServerCheckRsp) recvMsg;
										String value = msg.getCheck();
										if (value.equals("223")) {
											this.state = CreateState.RECVCONNECTMESSAGE;
											hc.center.CenterProto.CenterServerMessageAskReq.Builder builder = (hc.center.CenterProto.CenterServerMessageAskReq.Builder) CenterApp
													.getInstance().getServerConnectLogic()
													.getBuilder(CenterProtocol.CenterServerMessageAskReq);
											builder.setMsg("ask");
											builder.setServerID(CenterApp.getInstance().getServerID());
											builder.setServerName(CenterApp.getInstance().getServerName());
											session.getChannel()
													.writeAndFlush(ProtoHelper.createProtoBufByteBuf(
															CenterApp.getInstance().getServerID(), 0,
															CenterProtocol.CenterServerMessageAskReq,
															builder.build().toByteArray()));
										} else {
											session.getChannel().close();
										}
									});
						} else {
							session.getChannel().close();
						}
					} else {
						Trace.logger.info("协议解析错误");
					}
				} else {
					Trace.logger.info("暂时不支持其他类型的协议");
				}
			});
		} else if (this.state == CreateState.RECVCONNECTMESSAGE) {
			ProtoHelper.recvProtoBufByteBuf(buf, (result, srcID, desID, protoType, protoID, body) -> {
				if (protoType == ProtoType.PROTOBUF) {
					if (result) {
						if (protoID == CenterProtocol.CenterServerMessageAskRsp) {
							CenterApp.getInstance().getServerConnectLogic().recvMessage2Protocol(protoID, body,
									(recvMsg) -> {
										hc.center.CenterProto.CenterServerMessageAskRsp msg = (hc.center.CenterProto.CenterServerMessageAskRsp) recvMsg;
										int remoteServerID = msg.getServerID();
										int remoteServerType = msg.getServerType();
										String remoteServerName = msg.getServerName();
										String remoteServerJsonMsg = msg.getJsonMsg();
										Server remoteServer = null;
										ObjectMapper json = new ObjectMapper();
										try {
											if (remoteServerType == ServerType.GATE.ordinal()) {
												remoteServer = json.readValue(remoteServerJsonMsg, Gate.class);
											} else if (remoteServerType == ServerType.DATA.ordinal()) {
												remoteServer = json.readValue(remoteServerJsonMsg, Data.class);
											} else if (remoteServerType == ServerType.SCENE.ordinal()) {
												remoteServer = json.readValue(remoteServerJsonMsg, Scene.class);
											}
										} catch (Exception e) {
											Trace.logger.info(e);
											session.getChannel().close();
										}
										remoteServer.setServerId(remoteServerID);
										remoteServer.setServerName(remoteServerName);
										remoteServer.setOpen(false);
										CenterApp.getInstance().addServer(remoteServer);
									});
						} else {
							session.getChannel().close();
						}
					} else {
						Trace.logger.info("协议解析错误");
					}
				} else {
					Trace.logger.info("暂时不支持其他类型的协议");
				}
			});
		}
	}
}
