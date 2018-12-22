package point.login.base;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.google.protobuf.InvalidProtocolBufferException;
import com.hc.component.db.mysql.MysqlManager;
import com.hc.component.net.server.ServerManager;
import com.hc.component.net.session.Session;
import com.hc.share.util.Trace;

import hc.head.ProtoHead.Head.Builder;
import io.netty.buffer.ByteBuf;
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
	private HashMap<String, ModuleInterface> modules = new HashMap<>();
	private HashMap<Integer, ModuleInterface> protoBufProtocols = new HashMap<>();
	
	//注册模块 main
	public void registerModule(ModuleInterface module) {
		modules.put(module.getModuleName(), module);
	}
	public void registerProtoBufProtoProtocol(int pid, ModuleInterface logic) {
		
	}
	//添加链接
	public void addGateConnect(Session<byte[]> session) {
		Gate gate = new Gate(session);
		gates.put(session.getSessionID(), gate);
		for(Entry<String, ModuleInterface> logic : modules.entrySet()) {
			logic.getValue().onAddGateConnect(gate);
		}
		Trace.logger.info("add gate sessionid :" + session.getSessionID());
	}
	//断开连接
	public void removeGateConnect(Session<byte[]> session) {
		Gate gate = gates.remove(session.getSessionID());
		for(Entry<String, ModuleInterface> logic : modules.entrySet()) {
			logic.getValue().onRemoveGateConnect(gate);
		}
		Trace.logger.info("remove gate sessionid :" + session.getSessionID());
	}
	public void launchLogin() {
		for(Entry<String, ModuleInterface> logic : modules.entrySet()) {
			logic.getValue().onLaunchLogin();
		}
	}
	public void recvProto(Session<byte[]> session, ByteBuf buf) {
		short headLen = buf.readShort();
		buf = buf.slice(0, 2);
		byte[] bytes = new byte[headLen];
		buf.getBytes(headLen, bytes);
		buf.slice(0, headLen);
		try {
			Builder header = hc.head.ProtoHead.Head.newBuilder().mergeFrom(bytes);
			hc.head.ProtoHead.Head head = header.build();
			int protoID = head.getProtoID();
			if(head.getType() == hc.head.ProtoHead.Head.ProtoType.PROTOBUF) {
				appExec.execute(()->{ //执行器提供登陆服务
					
				});
			}else {
				Trace.logger.warn("sessionID:" + session.getSessionID() + "protocol type error");
				session.getChannel().close();
			}
		} catch (InvalidProtocolBufferException e) {
			session.getChannel().close();
			e.printStackTrace();
		}		
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
