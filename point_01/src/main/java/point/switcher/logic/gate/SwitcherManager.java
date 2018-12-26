package point.switcher.logic.gate;

import com.hc.component.net.session.Session;
import com.hc.share.service.Login;
import com.hc.share.service.Server;

import io.netty.buffer.ByteBuf;
import point.switcher.logic.GateModule;

public class SwitcherManager implements GateModule {
	private String name = "switcher";
	@Override
	public String getModuleName() {
		return this.name;
	}
	@Override
	public void onLaunchGate() {
		
	}
	@Override
	public void onLoginConnect(Login login) {
		
	}
	@Override
	public void onLoginUnConnect() {
		
	}

	@Override
	public void onAddServer(long serverID, Server server) {
		
	}
	@Override
	public void onRemoveServer(int serverID) {
		
	}
	@Override
	public void OnClientProto(Session session, ByteBuf buf) {
		
	}

	@Override
	public void OnServerProto(Server server, ByteBuf buf) {
		
	}
}
