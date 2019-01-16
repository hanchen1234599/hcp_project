package com.hc.component.net.client.base;

import com.hc.component.net.client.ClientListener;
import com.hc.component.net.client.ClientManager;
import com.hc.component.net.session.Session;

import io.netty.buffer.ByteBuf;

public class ClientManagerImpl implements ClientManager {
	private Session session = null;
	private ClientListener listener = null;
	private NettyClient client = null;
	private volatile boolean isOpen = false; 
	public ClientManagerImpl(String host, int port, int workeThreadNum, int inProtoLength, int outProtoLength) {
		this.client = new NettyClient(host, port, workeThreadNum, inProtoLength, outProtoLength, this);
	}
	public void connect(Session session) {
		this.session  = session;
		this.listener.onConnect(this.session);
	}
	public void unConnect(Session session) {
		this.listener.onUnConnect( session );
		this.session = null;
	}
	@Override
	public void registListener(ClientListener listener) {
		this.listener = listener;
	}

	@Override
	public void open() throws Exception {
		if(this.isOpen == true)
			return;
		this.client.beginConnect();
		this.isOpen = true;
	}
	
	public void onConnectException(Session session, Throwable cause) {
		this.session = null;
		this.listener.onConnectException(session, cause);
	}
	
	@Override
	public void close() {
		if(this.isOpen == false)
			return;
		this.client.close();
		this.session = null;
		this.isOpen = false;
		this.listener.onDestory(this);
	}

	@Override
	public Session getSession() {
		return this.session;
	}
	protected void recvData(Session session, ByteBuf msg) {
		this.listener.onData(session, msg);
	}

}
