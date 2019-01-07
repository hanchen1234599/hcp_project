package gate.logic;

import com.hc.component.net.session.Session;

import io.netty.buffer.ByteBuf;
import share.server.service.Login;
import share.server.service.Server;

public interface GateModule {
	String getModuleName();
	void onLaunchGate();
	void onLoginConnect(Login login);
	void onLoginUnConnect();
	void onAddServer(long serverID, Server server);
	void onRemoveServer(int serverID);
	void OnClientProto(Session session, ByteBuf buf);
	void OnServerProto(Server server, ByteBuf buf);
}
