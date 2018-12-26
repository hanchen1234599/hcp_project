package com.hc.component.net.websocket;

import com.hc.component.net.session.Session;
import com.hc.share.Manager;

public interface WebSocketManager extends Manager<WebSocketListener> {
	Session getSession(long sessionID);
}
