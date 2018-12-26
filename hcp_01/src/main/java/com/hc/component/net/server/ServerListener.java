package com.hc.component.net.server;

import com.hc.component.net.session.Session;
import com.hc.share.Listener;
import io.netty.buffer.ByteBuf;

public interface ServerListener extends Listener<ServerManager> {
	void onAddSession(Session session);
	void onRemoveSession(Session session);
	void onExceptionSession(Session session);
	void onData(Session session, ByteBuf body);
}
