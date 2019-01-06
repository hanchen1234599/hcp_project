package com.hc.component.net.server.base;

import com.hc.component.net.session.Session;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;

public class SessionImpl implements Session{
	private Object arg = null;
	private Channel channel = null;
	private long sessionID;
	public SessionImpl(Channel channel, long sessionID) {
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
		channel.writeAndFlush(body);
	}
	@Override
	public void setParamete(Object arg) {
		this.arg = arg;
	}
	@Override
	public Object getParamete() {
		return this.arg;
	}
}
