package com.hc.component.net.websocket;

import com.hc.component.net.session.Session;
import com.hc.share.Listener;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;

public interface WebSocketListener extends Listener<WebSocketManager> {
	void onAddSession(Session session);
	void onRemoveSession(Session session);
	void onData(Session session, ByteBuf body);
	void onHttp(Session session, FullHttpRequest req, FullHttpResponse rsp);
	void OnExceptionCaught(Session session, Throwable cause);
}
