package gate.logic.gate;

import com.hc.component.net.session.Session;

import gate.logic.GateModule;
import io.netty.buffer.ByteBuf;
import share.server.service.Login;
import share.server.service.Server;

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
