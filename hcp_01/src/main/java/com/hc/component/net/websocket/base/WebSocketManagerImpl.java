package com.hc.component.net.websocket.base;

import java.util.concurrent.ConcurrentHashMap;
import com.hc.component.net.session.Session;
import com.hc.component.net.session.SessionContainer;
import com.hc.component.net.websocket.WebSocketListener;
import com.hc.component.net.websocket.WebSocketManager;
import com.hc.share.exception.NetException;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;

public class WebSocketManagerImpl implements WebSocketManager, SessionContainer {
	private WebSocketServer server = null;
	private WebSocketListener listener = null;
	private ConcurrentHashMap<Long, Session> sessionContainerId = new ConcurrentHashMap<>();
	private ConcurrentHashMap<Channel, Session> sessionContainerChannel = new ConcurrentHashMap<>();
	
	public WebSocketManagerImpl(int port, int boosThreadNum, int workeThreadNum) {
		this.server = new WebSocketServer(port, boosThreadNum, workeThreadNum);
	}
	@Override
	public void registListener(WebSocketListener listener) {
		this.listener = listener;
	}
	@Override
	public void open() throws Exception {
		this.server.openListen(this);
	}
	@Override
	public void close() {
		this.server.close();
	}
	@Override
	public void addSession(Session session) throws NetException {
		synchronized (session) {
			this.sessionContainerId.put(session.getSessionID(), session);
			this.sessionContainerChannel.put(session.getChannel(), session);
		}
		this.listener.onAddSession(session);
	}
	@Override
	public void removeSession(long sessionID) {
		Session session = this.sessionContainerId.get(sessionID);
		synchronized (session) {
			this.sessionContainerId.remove(sessionID);
			this.sessionContainerChannel.remove(session.getChannel());
		}
		this.listener.onRemoveSession(session);
	}
	@Override
	public void removeSession(Session session) {
		synchronized (session) {
			this.sessionContainerId.remove(session.getSessionID());
			this.sessionContainerChannel.remove(session.getChannel());
		}
		this.listener.onRemoveSession(session);
	}
	@Override
	public void removeSession(Channel channel) {
		Session session = this.sessionContainerChannel.get(channel);
		synchronized (session) {
			this.sessionContainerId.remove(session.getSessionID());
			this.sessionContainerChannel.remove(channel);
		}
		this.listener.onRemoveSession(session);
	}
	@Override
	public void recvData(Session session, ByteBuf body) {
		this.listener.onData(session, body);
	}
	public void recvHttp(Session session, FullHttpRequest req, FullHttpResponse rsp) {
		this.listener.onHttp(session, req, rsp);
	}
	@Override
	public Session getSession(Channel channel) {
		return this.sessionContainerChannel.get(channel);
	}
	@Override
	public Session getSession(long sessionID) {
		return this.sessionContainerId.get(sessionID);
	}
}
