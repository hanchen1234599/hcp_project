package com.hc.component.net.server;

import com.hc.component.net.server.base.ServerManagerImpl;
import com.hc.share.Component;
import com.hc.share.exception.NetException;

public class ServerComponent extends Component<ServerManager, ServerListener> implements ServerBuilder {
	private int boosThreadNum = 1;
	private int workeThreadNum = 4;
	private int inProtoLength = 64 * 1024;
	private int outProtoLength  = 64 * 1024;
	private int port = 0;
	@Override
	public ServerBuilder setPort(int port) {
		this.port = port;
		return this;
	}
	@Override
	public ServerBuilder setEventLoop(int boosThreadNum, int workeThreadNum) {
		this.boosThreadNum = boosThreadNum;
		this.workeThreadNum = workeThreadNum;
		return this;
	}
	@Override
	public ServerBuilder setInProtoLength(int maxFrameLength) {
		this.inProtoLength = maxFrameLength;
		return this;
	}
	@Override
	public ServerBuilder setOutProtoLength(int maxFrameLength) {
		this.outProtoLength = maxFrameLength;
		return this;
	}
	@Override
	public void build() throws NetException {
		if(port == 0)
			throw new NetException("NettyServerImpl port is error");
		if(this.listener == null)
			throw new NetException("NettyServerImpl port is error");
		this.manager = new ServerManagerImpl(port, boosThreadNum, workeThreadNum, inProtoLength, outProtoLength);
		this.manager.registListener(listener);
		this.listener.onInit(this.manager);
	}
}
