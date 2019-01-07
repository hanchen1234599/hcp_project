package gate.passcheck;

import share.server.service.Server;
import share.server.service.ServerType;

public interface PassCheck {
	void addPass(Server server);
	Server getPass(ServerType type);
	long getUserID();
}
