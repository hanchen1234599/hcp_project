package com.hc.component.net.server;

import com.hc.component.net.session.Session;
import com.hc.share.Manager;

public interface ServerManager extends Manager<ServerListener> {
	Session getSession(long sessionID);
}
