package com.hc.component.net.server.base;

import java.util.concurrent.ConcurrentHashMap;

import com.hc.component.net.server.ServerListener;
import com.hc.component.net.server.ServerManager;
import com.hc.component.net.session.Session;
import com.hc.component.net.session.SessionContainer;
import com.hc.share.exception.NetException;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;

/**
 * sessionmanage
 * 
 * @author hanchen
 */
public class ServerManagerImpl implements ServerManager, SessionContainer {
	private ConcurrentHashMap<Long, Session> sessionContainerId = new ConcurrentHashMap<>();
	private ConcurrentHashMap<Channel, Session> sessionContainerChannel = new ConcurrentHashMap<>();
	private ServerListener listener = null;
	private NettyServer server = null;
	private volatile boolean isopen = false;
	public ServerManagerImpl(int port, int boosThreadNum, int workeThreadNum, int inProtoLength, int outProtoLength) {
		NettyServer.NettyServerImpl impl = new NettyServer.NettyServerImpl();
		impl.setBossThreadNum(boosThreadNum);
		impl.setWorkThreadNum(workeThreadNum);
		impl.setInProtoLength(inProtoLength);
		impl.setOutProtoLength(outProtoLength);
		impl.setPort(port);
		this.server = impl;
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
	public void recvData(Session session, ByteBuf bd) {
		this.listener.onData(session, bd);
	}
	@Override
	public void OnExceptionCaught(Session session, Throwable cause) {
		this.listener.OnExceptionCaught(session, cause);
	}
	@Override
	public Session getSession(Channel channel) {
		return this.sessionContainerChannel.get(channel);
	}

	@Override
	public Session getSession(long sessionID) {
		return this.sessionContainerId.get(sessionID);
	}

	@Override
	public void open() throws Exception {
		if(this.isopen == true)
			return;
		this.server.openListen(this);
		this.isopen = true;
	}

	@Override
	public void close() {
		if(this.isopen == false)
			return;
		this.server.close();
		this.isopen = false;
		this.listener.onDestory(this);
	}

	@Override
	public void registListener(ServerListener listener) {
		this.listener = listener;
	}
}
