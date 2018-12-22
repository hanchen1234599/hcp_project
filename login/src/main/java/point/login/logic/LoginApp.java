package point.login.logic;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.hc.component.db.mysql.MysqlManager;
import com.hc.component.net.server.ServerManager;
import com.hc.component.net.session.Session;
import com.hc.share.util.Trace;
/**
 * @author hanchen
 * 登陆应用 
 * 管理网关的连接与记录
 * 提供与给网关外网通行证
 * appExec 应用执行器应用逻辑都用这个执行器
 * gates gate容器 以sessionID 做为key
 */
//观察者的实现
public class LoginApp {
	private static LoginApp instance = new LoginApp();
	private MysqlManager db = null;
	private ServerManager server = null;
	@SuppressWarnings("unused")
	private ExecutorService appExec = Executors.newFixedThreadPool(4);
	private ConcurrentHashMap<Long, Gate> gates = new ConcurrentHashMap<>();
	private HashMap<String, LogicInterface> logics = new HashMap<>();
	
	//注册模块 main
	public void registreModule(LogicInterface logic) {
		logics.put(logic.getModuleName(), logic);
	}
	//添加链接
	public void addGateConnect(Session<byte[]> session) {
		Gate gate = new Gate(session);
		gates.put(session.getSessionID(), gate);
		for(Entry<String, LogicInterface> logic : logics.entrySet()) {
			logic.getValue().addGateConnect(gate);
		}
		Trace.logger.info("add gate sessionid :" + session.getSessionID());
	}
	//添加链接
	public void removeGateConnect(Session<byte[]> session) {
		Gate gate = gates.remove(session.getSessionID());
		for(Entry<String, LogicInterface> logic : logics.entrySet()) {
			logic.getValue().removeGateConnect(gate);
		}
		Trace.logger.info("remove gate sessionid :" + session.getSessionID());
	}
	
	public MysqlManager getDb() {
		return db;
	}
	public void setDb(MysqlManager db) {
		this.db = db;
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
}
