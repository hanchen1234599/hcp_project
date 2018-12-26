package com.hc.component.net.session;

import com.hc.share.service.Server;
import com.hc.share.service.ServerType;

public interface PassCheck {
	void addPass(Server server);
	Server checkPass(ServerType type);
	long getUserID();
}
