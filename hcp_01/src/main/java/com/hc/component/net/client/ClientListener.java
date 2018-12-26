package com.hc.component.net.client;

import com.hc.component.net.session.Session;
import com.hc.share.Listener;

import io.netty.buffer.ByteBuf;

public interface ClientListener extends Listener<ClientManager> {
	void onConnect(Session session);
	void onUnConnect();
	void onConnectException();
	void onData(Session session, ByteBuf body);
}
