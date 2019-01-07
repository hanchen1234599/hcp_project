package gate.passcheck;

import java.util.concurrent.ConcurrentHashMap;

import share.server.service.Center;
import share.server.service.Data;
import share.server.service.Scene;
import share.server.service.Server;
import share.server.service.ServerType;
/**
 * @author hanchen
 * 账号通行证
 */
public class AccountPass implements PassCheck {
	private ConcurrentHashMap<ServerType, Server> powers = new ConcurrentHashMap<>();
	private long userID = 0;
	
	@Override
	public void addPass(Server server) {
		ServerType type = ServerType.CENTER;
		if(server instanceof Center) {
			type = ServerType.CENTER;
		}else if( server instanceof Data ) {
			type = ServerType.DATA;
		}else if( server instanceof Scene ) {
			type = ServerType.SCENE;
		}
		if(powers.get(type) != null)
			powers.remove(type);
		powers.put(type, server);
	}
	@Override
	public Server getPass(ServerType type) {
		return powers.get(type);
	}
	public long getUserID() {
		return userID;
	}
	public void setUserID(long userID) {
		this.userID = userID;
	}
}
