package com.hc.component.net.websocket.base;

import com.hc.component.net.session.PassCheck;
import com.hc.component.net.session.Session;
import com.hc.component.net.websocket.WebSocket;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;

public class WebSocketSessionImpl implements Session {
	private PassCheck pass = null; // 通行证
	private Channel channel;
	private long sessionID;
	public WebSocketSessionImpl(Channel channel, long sessionID) {
		this.channel = channel;
		this.sessionID = sessionID;
	}
	@Override
	public long getSessionID() {
		return this.sessionID;
	}
	@Override
	public Channel getChannel() {
		return this.channel;
	}
	@Override
	public void send(ByteBuf body) {
		WebSocket.sendTextWebSocketFrame(channel, body);
	}
	@Override
	public PassCheck getPassCheck() {
		return this.pass;
	}
	@Override
	public void setPassCheck(PassCheck pass) {
		this.pass = pass;
	}
}
