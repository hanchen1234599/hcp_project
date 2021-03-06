package gate;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.dom4j.Element;
import com.google.protobuf.InvalidProtocolBufferException;
import com.hc.component.net.client.ClientComponent;
import com.hc.component.net.server.ServerComponent;
import com.hc.component.net.server.ServerManager;
import com.hc.component.net.session.Session;
import com.hc.share.util.Trace;
import com.hc.share.util.XmlReader;
import gate.logic.GateModule;
import gate.passcheck.AccountPass;
import gate.passcheck.PassCheck;
import hc.head.ProtoHead.Head.ProtoType;
import io.netty.buffer.ByteBuf;
import share.proto.config.base.ProtocolLogic;
import share.proto.util.ProtoHelper;
import share.server.service.Center;
import share.server.service.Login;
import share.server.service.Server;

public class GateApp {
	private int serverID = 0;
	private String serverName = "";
	private String serverMsg = "";

	private static GateApp instance = new GateApp();

	private GateApp() {
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
		if (server instanceof Center) {
			if (this.centerID == 0) {
				this.centerID = serverID;
			} else {
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
		if (server instanceof Center) {
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
		if (session.getParamete() != null)
			this.clients.remove(((PassCheck) (session.getParamete())).getUserID());
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
				if (protoType == ProtoType.PROTOBUF && protoID == share.proto.config.LoginProtocol.LoginPessRsp) {
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
							if (clientSession.getParamete() != null) {
								Trace.logger.info("userID:" + rspUserID + "重复登陆");
								return;
							}
							if (clientSession != null && clientSession.getChannel().isActive()) {
								// Center center = getCenterServer();
								// if(center == null) {
								// clientSession.getChannel().close();
								// return;
								// }

								AccountPass pass = new AccountPass();
								pass.setUserID(rspUserID);
								// pass.addPass(center);
								clientSession.setParamete(pass);
								rspBuilder.setUserID(rspUserID);
								clientSession.send(ProtoHelper.createProtoBufByteBuf(
										GateApp.getInstance().getServerID(), 0,
										share.proto.config.LoginProtocol.LoginRsp, rspBuilder.build().toByteArray()));
								Trace.logger
										.info("usreID: " + rspUserID + " 登陆成功" + " time:" + System.currentTimeMillis());
							}
						} else {
							rspBuilder.setUserID(0);
							clientSession.send(ProtoHelper.createProtoBufByteBuf(GateApp.getInstance().getServerID(), 0,
									share.proto.config.LoginProtocol.LoginRsp, rspBuilder.build().toByteArray()));
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
		if (server != null)
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
		
	}
}
