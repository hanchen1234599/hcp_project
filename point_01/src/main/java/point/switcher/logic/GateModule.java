package point.switcher.logic;

import com.hc.component.net.session.Session;
import com.hc.share.service.Login;
import com.hc.share.service.Server;

import io.netty.buffer.ByteBuf;

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
