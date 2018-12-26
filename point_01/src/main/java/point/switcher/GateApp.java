package point.switcher;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.google.protobuf.InvalidProtocolBufferException;
import com.hc.component.net.server.ServerManager;
import com.hc.component.net.session.Session;
import com.hc.share.service.Client;
import com.hc.share.service.Login;
import com.hc.share.service.Server;
import com.hc.share.util.ProtoHelper;
import com.hc.share.util.Trace;

import hc.head.ProtoHead.Head.ProtoType;
import io.netty.buffer.ByteBuf;
import point.switcher.logic.GateModule;
import point.switcher.passcheck.AccountPass;

public class GateApp {
	private int curServiceID = 0;
	private static GateApp instace = new GateApp();

	private GateApp() {
	}

	public static GateApp getInstace() {
		return instace;
	}

	private ServerManager outerManager = null;
	private ServerManager innerManager = null;
	private Login login = null;
	private ExecutorService appExec = Executors.newFixedThreadPool(4);
	private HashMap<String, GateModule> modules = new HashMap<>();
	private HashMap<Integer, GateModule> protoBufProtocols = new HashMap<>();
	private ConcurrentHashMap<Long, Client> sessionID2Client = new ConcurrentHashMap<>();
	private ConcurrentHashMap<Long, Client> userID2Client = new ConcurrentHashMap<>();
	private ConcurrentHashMap<Integer, Server> servers = new ConcurrentHashMap<>();

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
		this.servers.put(serverID, server);
		for (Entry<String, GateModule> logic : modules.entrySet()) {
			logic.getValue().onAddServer(serverID, server);
		}
	}

	public void onRemoveServer(int serverID) {
		this.servers.remove(serverID);
		for (Entry<String, GateModule> logic : modules.entrySet()) {
			logic.getValue().onRemoveServer(serverID);
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

	public void onClientUnactive(long sessionID) {
		Trace.logger.debug("客户端断开   gate sessionID:" + sessionID);
		Client client = this.sessionID2Client.remove(sessionID);
		if (client != null) {
			Trace.logger.info("userID: " + client.getSession().getPassCheck().getUserID() + " 断开链接");
			userID2Client.remove(client.getSession().getPassCheck().getUserID());
		}
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
				if (protoType == ProtoType.PROTOBUF && protoID == 13) {
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
								Client client = new Client(clientSession);
								AccountPass pass = new AccountPass();
								pass.setUserID(rspUserID);
								// 这里找到center的server添加
								// pass.addPass(server);
								clientSession.setPassCheck(pass);
								userID2Client.put(rspUserID, client);
								sessionID2Client.put(clientSession.getSessionID(), client);
								// 给客户端回应
								rspBuilder.setUserID(rspUserID);
								clientSession.send(ProtoHelper.createProtoBufByteBuf(GateApp.getInstace().getCurServiceID(), 0, 14, rspBuilder.build().toByteArray()));
								Trace.logger.info("usreID: " + rspUserID + " 登陆成功" + " time:" + System.currentTimeMillis() );
							}
						} else {
							rspBuilder.setUserID(0);
							clientSession.send(ProtoHelper.createProtoBufByteBuf(GateApp.getInstace().getCurServiceID(), 0, 14, rspBuilder.build().toByteArray()));
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
		if (session.getPassCheck() == null) {
			ProtoHelper.recvProtoBufByteBuf(buf, (result, srcID, desID, protoType, protoID, body) -> {
				if (result) {
					if (protoType == ProtoType.PROTOBUF && protoID == 11) {
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
							this.login.getSession().send(ProtoHelper.createProtoBufByteBuf(this.curServiceID, 0, 12,
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
			this.appExec.execute(() -> {
//				for (Entry<String, GateModule> logic : modules.entrySet()) {
//					logic.getValue().OnClientProto(session, buf);
//				}
				
				ProtoHelper.recvProtoBufByteBuf(buf, (result, srcID, desID, protoType, protoID, body) -> {
					if (result) {
						if (protoType == ProtoType.PROTOBUF && protoID == 100) {
							int bodyLen = body.readableBytes();
							byte[] reqbody = new byte[bodyLen];
							body.getBytes(0, reqbody);
							try {
								hc.gate.S2GConnect.Ping testReq = hc.gate.S2GConnect.Ping.newBuilder().mergeFrom(reqbody).build();
								int index = testReq.getResult();
								index ++;
								hc.gate.S2GConnect.Ping.Builder testRspBuilder = hc.gate.S2GConnect.Ping.newBuilder();
								testRspBuilder.setResult(index);
								session.send(ProtoHelper.createProtoBufByteBuf(this.curServiceID, 0, 100,testRspBuilder.build().toByteArray()));
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
					
			});
		}
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

	public int getCurServiceID() {
		return curServiceID;
	}

	public void setCurServiceID(int curServiceID) {
		this.curServiceID = curServiceID;
	}
}
