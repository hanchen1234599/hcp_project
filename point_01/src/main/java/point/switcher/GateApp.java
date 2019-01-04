package point.switcher;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.dom4j.Element;
import com.google.protobuf.InvalidProtocolBufferException;
import com.hc.component.net.client.ClientComponent;
import com.hc.component.net.client.ClientListener;
import com.hc.component.net.server.ServerComponent;
import com.hc.component.net.server.ServerListener;
import com.hc.component.net.server.ServerManager;
import com.hc.component.net.session.Session;
import com.hc.share.service.Center;
import com.hc.share.service.Login;
import com.hc.share.service.Server;
import com.hc.share.service.ServerType;
import com.hc.share.util.ProtoHelper;
import com.hc.share.util.Trace;
import com.hc.share.util.XmlReader;
import hc.head.ProtoHead.Head.ProtoType;
import hc.protoconfig.CenterProtocol;
import hc.protoconfig.ProtocolLogic;
import io.netty.buffer.ByteBuf;
import point.switcher.logic.GateModule;
import point.switcher.passcheck.AccountPass;
import serverType.ClientConfig;
import serverType.ServerConfig;

public class GateApp {
	private int serverID = 0;
	private String serverName = "";
	private String serverMsg = "";


	private static GateApp instance = new GateApp();

	private GateApp( ) {
	}

	public static GateApp getInstance() {
		return instance;
	}
	
	private Login login = null;
	private ExecutorService appExec = null;
	private ServerManager outerManager = null;
	private ServerManager innerManager = null;
	private HashMap<String, GateModule> modules = new HashMap<>();
	private HashMap<Integer, GateModule> protoBufProtocols = new HashMap<>();
	private ConcurrentHashMap<Long, Session> clients = new ConcurrentHashMap<>(); // userid - > session
	private ConcurrentHashMap<Integer, Server> servers = new ConcurrentHashMap<>();
	private int centerID = 0;  
	
	// 注册模块 main
	public void registerModule(GateModule module) {
		this.modules.put(module.getModuleName(), module);
	}
	// 给模块注册协议
	public void registerProtoBufProtoProtocol(int pid, GateModule logic) {
		if (this.protoBufProtocols.get(pid) != null) {
			Trace.logger.error("协议id: " + pid + " 重复注册");
			Runtime.getRuntime().exit(1);
		}
		this.protoBufProtocols.put(pid, logic);
	}

	// 服务器初始化
	public void onAddServer(int serverID, Server server) {
		if(server instanceof Center) {
			if(this.centerID == 0) {
				this.centerID = serverID;
			}else {
				server.getSession().getChannel().close();
				return;
			}
		}
		this.servers.put(serverID, server);
		for (Entry<String, GateModule> logic : modules.entrySet()) {
			logic.getValue().onAddServer(serverID, server);
		}
	}

	public void onRemoveServer(Server server) {
		if(server instanceof Center) {
			this.centerID = 0;
		}
		this.servers.remove(server.getServerId());
		for (Entry<String, GateModule> logic : modules.entrySet()) {
			logic.getValue().onRemoveServer(server.getServerId());
		}
	}

	public void launchGate() {
		for (Entry<String, GateModule> logic : modules.entrySet()) {
			logic.getValue().onLaunchGate();
		}
	}

	public void onClientInactive(Session session) {
		Trace.logger.info("客户端连接到gate sessionID:" + session.getSessionID() + " time:" + System.currentTimeMillis());
	}

	public void onClientUnactive(Session session) {
		Trace.logger.debug("客户端断开   gate sessionID:" + session.getSessionID());
		if(session.getPassCheck() != null)
			this.clients.remove(session.getPassCheck().getUserID());
	}

	public void recvServerProto(Server server, ByteBuf buf) {
		appExec.execute(() -> {
			for (Entry<String, GateModule> logic : modules.entrySet()) {
				logic.getValue().OnServerProto(server, buf);
			}
		});
	}
	// 收到login service的协议
	public void recvLoginProto(Session session, ByteBuf buf) {
		ProtoHelper.recvProtoBufByteBuf(buf, (result, srcID, desID, protoType, protoID, body) -> {
			if (result) {
				if (protoType == ProtoType.PROTOBUF && protoID == hc.protoconfig.LoginProtocol.LoginPessRsp) {
					int bodyLen = body.readableBytes();
					byte[] bodyBuff = new byte[bodyLen];
					body.getBytes(0, bodyBuff);
					try {
						hc.login.PessCheck.LoginPessRsp loginPessRsp = hc.login.PessCheck.LoginPessRsp.newBuilder()
								.mergeFrom(bodyBuff).build();
						int rspResult = loginPessRsp.getResult();
						hc.login.PessCheck.LoginRsp.Builder rspBuilder = hc.login.PessCheck.LoginRsp.newBuilder();
						long rspSessionID = loginPessRsp.getSessionID();
						Session clientSession = outerManager.getSession(rspSessionID);
						rspBuilder.setResult(rspResult);
						if (rspResult == 0) {
							long rspUserID = loginPessRsp.getUserID();
							if (clientSession == null)
								return;
							if (clientSession.getPassCheck() != null) {
								Trace.logger.info("userID:" + rspUserID + "重复登陆");
								return;
							}
							if (clientSession != null && clientSession.getChannel().isActive()) {
								//Center center = getCenterServer();
//								if(center == null) {
//									clientSession.getChannel().close();
//									return;
//								}
								
								AccountPass pass = new AccountPass();
								pass.setUserID(rspUserID);
								//pass.addPass(center);
								clientSession.setPassCheck(pass);
								rspBuilder.setUserID(rspUserID);
								clientSession.send(ProtoHelper.createProtoBufByteBuf(GateApp.getInstance().getServerID(), 0, hc.protoconfig.LoginProtocol.LoginRsp, rspBuilder.build().toByteArray()));
								Trace.logger.info("usreID: " + rspUserID + " 登陆成功" + " time:" + System.currentTimeMillis() );
							}
						} else {
							rspBuilder.setUserID(0);
							clientSession.send(ProtoHelper.createProtoBufByteBuf(GateApp.getInstance().getServerID(), 0, hc.protoconfig.LoginProtocol.LoginRsp, rspBuilder.build().toByteArray()));
						}
					} catch (InvalidProtocolBufferException e) {
						Trace.logger.info("login 登陆协议解析错误");
						e.printStackTrace();
					}
				}
			} else {
				Trace.logger.info("login 登陆头协议解析错误");
			}
		});
	}

	public void recvClientProto(Session session, ByteBuf buf) {
		this.appExec.execute(() -> {				
					
		});
	}

	public void setLogin(Login login) {
		this.login = login;
		if (login != null) {
			for (Entry<String, GateModule> logic : modules.entrySet()) {
				logic.getValue().onLoginConnect(login);
			}
		} else {
			for (Entry<String, GateModule> logic : modules.entrySet()) {
				logic.getValue().onLoginUnConnect();
			}
		}
	}
	
	public void setAppNThead(int nThread) {
		this.appExec = Executors.newFixedThreadPool(nThread);
	}
	
	public Center getCenterServer() {
		Server server = servers.get(this.centerID);
		if(server != null)
			return (Center) server;
		else
			return null;
	}
	
	public Login getLogin() {
		return login;
	}

	public void setOuterManager(ServerManager outerManager) {
		this.outerManager = outerManager;
	}

	public void setInnerManager(ServerManager innerManager) {
		this.innerManager = innerManager;
	}

	public ServerManager getOuterManager() {
		return outerManager;
	}

	public ServerManager getInnerManager() {
		return innerManager;
	}

	public int getServerID() {
		return serverID;
	}

	public void setServerID(int curServiceID) {
		this.serverID = curServiceID;
	}
	public String getServerName() {
		return serverName;
	}
	public void setServerName(String serverName) {
		this.serverName = serverName;
	}
	public String getServerMsg() {
		return serverMsg;
	}
	public void setServerMsg(String serverMsg) {
		this.serverMsg = serverMsg;
	}
	// 连接center服务器
	
	private ProtocolLogic serverinnerLogic = new ProtocolLogic();
	
	public void start() {
		this.serverinnerLogic.init();
		this.serverinnerLogic.setExec(Executors.newSingleThreadExecutor());
	}
	public ProtocolLogic getServerInnerLogic() {
		return this.serverinnerLogic;
	}
	public void init(int serverID) throws Exception {
		Element serverTypeRoot = XmlReader.getInstance().readFile("../share/config/servertypeconfig.xml").getRootElement();
		Element serverTypeGate = XmlReader.getElementByAttributeWithElementName(serverTypeRoot, "servertype", "type", "gate");
		Element serverTypeInner = XmlReader.getElementByAttributeWithElementName(serverTypeGate, "component", "name", "inner");
		Element serverTypeOuter = XmlReader.getElementByAttributeWithElementName(serverTypeGate, "component", "name", "outer");
		Element serverType2Union = XmlReader.getElementByAttributeWithElementName(serverTypeGate, "component", "name", "union");
		Element serverType2Cente = XmlReader.getElementByAttributeWithElementName(serverTypeGate, "component", "name", "center");
		ServerConfig innerServerConfig = new ServerConfig();
		innerServerConfig.setBoosThreadNum(Integer.parseInt(serverTypeInner.attribute("boosthreadnum").getText()));
		innerServerConfig.setInProtoLength(Integer.parseInt(serverTypeInner.attribute("inprotolength").getText()));
		innerServerConfig.setListener(serverTypeInner.attribute("listener").getText());
		innerServerConfig.setOutProtoLength(Integer.parseInt(serverTypeInner.attribute("outprotolength").getText()));
		innerServerConfig.setWorkeThreadNum(Integer.parseInt(serverTypeInner.attribute("workethreadnum").getText()));
		ServerConfig outerServerConfig = new ServerConfig();
		outerServerConfig.setBoosThreadNum(Integer.parseInt(serverTypeOuter.attribute("boosthreadnum").getText()));
		outerServerConfig.setInProtoLength(Integer.parseInt(serverTypeOuter.attribute("inprotolength").getText()));
		outerServerConfig.setListener(serverTypeOuter.attribute("listener").getText());
		outerServerConfig.setOutProtoLength(Integer.parseInt(serverTypeOuter.attribute("outprotolength").getText()));
		outerServerConfig.setWorkeThreadNum(Integer.parseInt(serverTypeOuter.attribute("workethreadnum").getText()));
		ClientConfig gate2UnionConfig = new ClientConfig();
		gate2UnionConfig.setInProtoLength(Integer.parseInt(serverType2Union.attribute("inprotolength").getText()));
		gate2UnionConfig.setOutProtoLength(Integer.parseInt(serverType2Union.attribute("outprotolength").getText()));
		gate2UnionConfig.setWorkeThreadNum(Integer.parseInt(serverType2Union.attribute("workethreadnum").getText()));
		gate2UnionConfig.setListener(serverType2Union.attribute("listener").getText());
		ClientConfig gate2CenterConfig = new ClientConfig();
		gate2CenterConfig.setInProtoLength(Integer.parseInt(serverType2Cente.attribute("inprotolength").getText()));
		gate2CenterConfig.setOutProtoLength(Integer.parseInt(serverType2Cente.attribute("outprotolength").getText()));
		gate2CenterConfig.setWorkeThreadNum(Integer.parseInt(serverType2Cente.attribute("workethreadnum").getText()));
		gate2CenterConfig.setListener(serverType2Cente.attribute("listener").getText());
		
		Element serverConfigRoot = XmlReader.getInstance().readFile("../share/config/serverconfig.xml").getRootElement();
		Element gatePoint = XmlReader.getElementByAttributeWithElementName(serverConfigRoot, "point", "serverid", "" + serverID);
		this.serverID = serverID;
		this.serverName = gatePoint.attribute("name").getText();
		Element inner = gatePoint.element("inner");
		Element outer = gatePoint.element("outer");
		Element union = gatePoint.element("union");
		Element center = gatePoint.element("center");
		ServerComponent innerServerComponent = new ServerComponent();
		ServerComponent outerServerComponent = new ServerComponent();
		ClientComponent unionClientComponent = new ClientComponent();
		ClientComponent centerClientComponent = new ClientComponent();
		innerServerComponent.setEventLoop(innerServerConfig.getBoosThreadNum(), innerServerConfig.getWorkeThreadNum());
		innerServerComponent.setInProtoLength(innerServerConfig.getInProtoLength());
		innerServerComponent.setOutProtoLength(innerServerConfig.getOutProtoLength());
		innerServerComponent.setListener((ServerListener) Class.forName(innerServerConfig.getListener()).newInstance());
		innerServerComponent.setPort(Integer.parseInt(inner.attribute("port").getText()));
		innerServerComponent.build(); 
		outerServerComponent.setEventLoop(outerServerConfig.getBoosThreadNum(), outerServerConfig.getWorkeThreadNum());
		outerServerComponent.setInProtoLength(outerServerConfig.getInProtoLength());
		outerServerComponent.setOutProtoLength(outerServerConfig.getOutProtoLength());
		outerServerComponent.setListener((ServerListener) Class.forName(outerServerConfig.getListener()).newInstance());
		outerServerComponent.setPort(Integer.parseInt(outer.attribute("port").getText()));
		outerServerComponent.build();
		unionClientComponent.setEventLoop(gate2UnionConfig.getWorkeThreadNum());
		unionClientComponent.setInProtoLength(gate2UnionConfig.getInProtoLength());
		unionClientComponent.setOutProtoLength(gate2UnionConfig.getOutProtoLength());
		unionClientComponent.setListener((ClientListener)Class.forName(gate2UnionConfig.getListener()).newInstance());
		unionClientComponent.setConnect(union.attribute("remoteip").getText(), Integer.parseInt(outer.attribute("remoteport").getText()));
		unionClientComponent.build();
		centerClientComponent.setEventLoop(gate2CenterConfig.getWorkeThreadNum());
		centerClientComponent.setInProtoLength(gate2CenterConfig.getInProtoLength());
		centerClientComponent.setOutProtoLength(gate2CenterConfig.getOutProtoLength());
		centerClientComponent.setListener((ClientListener)Class.forName(gate2CenterConfig.getListener()).newInstance());
		centerClientComponent.setConnect(center.attribute("remoteip").getText(), Integer.parseInt(center.attribute("remoteport").getText()));
		centerClientComponent.build();
		//this.serverMsg = "";
		
	}
}
class ConnectCenter{
	static enum CreateState{ CREATECONNECT, CHECKCONNECT, RECVCONNECTMESSAGE, READY }
	private CreateState state = CreateState.CREATECONNECT;
	public void tryCreate( Session session, ByteBuf buf ) {
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
								if(msg.getMsg().equals("ask")) {
									hc.center.CenterProto.CenterServerMessageAskRsp.Builder builder = (hc.center.CenterProto.CenterServerMessageAskRsp.Builder) GateApp.getInstance().getServerInnerLogic().getBuilder(CenterProtocol.CenterServerMessageAskRsp);
									builder.setServerID(GateApp.getInstance().getServerID());
									builder.setServerType(ServerType.GATE.ordinal());
									builder.setServerName(GateApp.getInstance().getServerName());
									builder.setJsonMsg(GateApp.getInstance().getServerMsg());
									session.getChannel().writeAndFlush(ProtoHelper.createProtoBufByteBuf(GateApp.getInstance().getServerID(), 0, CenterProtocol.CenterServerMessageAskRsp, builder.build().toByteArray()));
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
