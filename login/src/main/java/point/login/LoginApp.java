package point.login;

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
import point.login.base.Gate;
import point.login.base.LoginModule;
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
	private ExecutorService appExec = Executors.newFixedThreadPool(4);
	private ConcurrentHashMap<Long, Gate> gates = new ConcurrentHashMap<>();    
	private HashMap<String, LoginModule> modules = new HashMap<>();
	private HashMap<Integer, LoginModule> protoBufProtocols = new HashMap<>();
	//注册模块 main
	public void registerModule(LoginModule module) {
		this.modules.put(module.getModuleName(), module);
	}
	//给模块注册协议
	public void registerProtoBufProtoProtocol(int pid, LoginModule logic) {
		if(this.protoBufProtocols.get(pid) != null) {
			Trace.logger.error("协议id: " + pid + " 重复注册");
			Runtime.getRuntime().exit(1);
		}
		this.protoBufProtocols.put(pid, logic);
	}
	//服务器初始化
	public void launchLogin() {
		for(Entry<String, LoginModule> logic : modules.entrySet()) {
			logic.getValue().onLaunchLogin();
		}
	}
	//添加链接
	public void addGateConnect(Session<byte[]> session) {
		Gate gate = new Gate(session);
		gates.put(session.getSessionID(), gate);
		for(Entry<String, LoginModule> logic : modules.entrySet()) {
			logic.getValue().onAddGateConnect(gate);
		}
		Trace.logger.info("add gate sessionid :" + session.getSessionID());
	}
	//断开连接
	public void removeGateConnect(Session<byte[]> session) {
		Gate gate = gates.remove(session.getSessionID());
		for(Entry<String, LoginModule> logic : modules.entrySet()) {
			logic.getValue().onRemoveGateConnect(gate);
		}
		Trace.logger.info("remove gate sessionid :" + session.getSessionID());
	}
	
	//收到协议
	public void recvProto(Session<byte[]> session, ByteBuf buf) {
		int bufLenght = buf.readableBytes();
		short headLen = buf.readShort();
		ByteBuf bufHead = buf.slice(2, headLen);
		byte[] bytes = new byte[headLen];
		bufHead.getBytes(0, bytes);
		buf.slice(0, headLen);
		try {
			Builder header = hc.head.ProtoHead.Head.newBuilder().mergeFrom(bytes);
			hc.head.ProtoHead.Head head = header.build();
			int protoID = head.getProtoID();
			if(head.getType() == hc.head.ProtoHead.Head.ProtoType.PROTOBUF) {
				byte[] body = new byte[bufLenght - 2 - headLen];
				buf.getBytes(2 + headLen, body, 0, body.length);
				appExec.execute(()->{ //执行器提供登陆服务	
					protoBufProtocols.get(protoID).onProtoBuf(session, protoID, body);
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
		if(db != null) {
			for(Entry<String, LoginModule> logic : modules.entrySet()) {
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
}
