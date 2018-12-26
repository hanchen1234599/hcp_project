package point.login;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.hc.component.db.mysql.MysqlManager;
import com.hc.component.net.server.ServerManager;
import com.hc.component.net.session.Session;
import com.hc.share.service.Gate;
import com.hc.share.util.ProtoHelper;
import com.hc.share.util.Trace;
import hc.head.ProtoHead.Head.ProtoType;
import io.netty.buffer.ByteBuf;
import point.login.base.LoginModule;

/**
 * @author hanchen 登陆应用 管理网关的连接与记录 提供与给网关外网通行证 appExec 应用执行器应用逻辑都用这个执行器 gates
 *         gate容器 以sessionID 做为key
 */
// 观察者的实现
public class LoginApp {
	private static LoginApp instance = new LoginApp();
	private MysqlManager db = null;
	private ServerManager server = null;
	private ExecutorService appExec = Executors.newFixedThreadPool(4);
	private ConcurrentHashMap<Long, Gate> gates = new ConcurrentHashMap<>();
	private HashMap<String, LoginModule> modules = new HashMap<>();
	private HashMap<Integer, LoginModule> protoBufProtocols = new HashMap<>();

	// 注册模块 main
	public void registerModule(LoginModule module) {
		this.modules.put(module.getModuleName(), module);
	}
	// 给模块注册协议
	public void registerProtoBufProtoProtocol(int pid, LoginModule logic) {
		if (this.protoBufProtocols.get(pid) != null) {
			Trace.logger.error("协议id: " + pid + " 重复注册");
			Runtime.getRuntime().exit(1);
		}
		this.protoBufProtocols.put(pid, logic);
	}
	// 服务器初始化
	public void launchLogin() {
		for (Entry<String, LoginModule> logic : modules.entrySet()) {
			logic.getValue().onLaunchLogin();
		}
	}
	// 添加链接
	public void addGateConnect(Session session) {
		Gate gate = new Gate(session);
		gates.put(session.getSessionID(), gate);
		Trace.logger.info("onAddGateConnect sessionid :" + session.getSessionID());
		for (Entry<String, LoginModule> logic : modules.entrySet()) {
			logic.getValue().onAddGateConnect(gate);
		}
	}
	// 断开连接
	public void removeGateConnect(Session session) {
		Gate gate = gates.remove(session.getSessionID());
		for (Entry<String, LoginModule> logic : modules.entrySet()) {
			logic.getValue().onRemoveGateConnect(gate);
		}
		Trace.logger.info("remove gate sessionid :" + session.getSessionID());
	}
	// 收到协议
	public void recvGateProto(Session session, ByteBuf buf) {
		ProtoHelper.recvProtoBufByteBuf(buf, (result, srcID, desID, protoType, protoID, body)->{
			if (protoType == ProtoType.PROTOBUF) {
				if(result) {
					appExec.execute(() -> { // 执行器提供登陆服务
						protoBufProtocols.get(protoID).onGateProto(session, protoID, body);
					});
				}else {
					Trace.logger.info("Gate 发来的数据头协议解析错误");
				}
			}else {
				Trace.logger.info("暂时不支持其他类型的协议");
			}
		});
	}
	public MysqlManager getDb() {
		return db;
	}
	public void setDb(MysqlManager db) {
		this.db = db;
		if (db != null) {
			for (Entry<String, LoginModule> logic : modules.entrySet()) {
				logic.getValue().onDbComplate();
			}
		}
	}
	public ServerManager getServer() {
		return server;
	}
	public void setServer(ServerManager server) {
		this.server = server;
	}
	public static LoginApp getInstace() {
		return instance;
	}
	public ExecutorService getAppExec() {
		return appExec;
	}
}
