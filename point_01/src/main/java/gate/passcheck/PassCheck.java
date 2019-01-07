package gate.passcheck;

import hc.server.service.Server;
import hc.server.service.ServerType;

public interface PassCheck {
	void addPass(Server server);
	Server getPass(ServerType type);
	long getUserID();
}
