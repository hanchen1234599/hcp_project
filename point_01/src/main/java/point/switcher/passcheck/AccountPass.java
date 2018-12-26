package point.switcher.passcheck;

import java.util.concurrent.ConcurrentHashMap;
import com.hc.component.net.session.PassCheck;
import com.hc.share.service.Center;
import com.hc.share.service.Data;
import com.hc.share.service.Scene;
import com.hc.share.service.Server;
import com.hc.share.service.ServerType;
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
	public Server checkPass(ServerType type) {
		return powers.get(type);
	}
	public long getUserID() {
		return userID;
	}
	public void setUserID(long userID) {
		this.userID = userID;
	}
}
