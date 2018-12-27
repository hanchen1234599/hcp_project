package com.hc.component.net.server;

import org.dom4j.Element;

import com.hc.component.ComponentType;
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
	public void setListener(ServerListener listener) {
		this.listener = listener;
	}
	@Override
	public void build() throws NetException {
		this.setType(ComponentType.SERVER);
		if(port == 0)
			throw new NetException("NettyServerImpl port is error");
		if(this.listener == null)
			throw new NetException("NettyServerImpl port is error");
		this.manager = new ServerManagerImpl(port, boosThreadNum, workeThreadNum, inProtoLength, outProtoLength);
		this.manager.registListener(listener);
		this.listener.onInit(this.manager);
	}
	@Override
	public void build(Element element) throws Exception {
		if(element.attributeValue("boosthreadnum") != null)
			this.boosThreadNum = Integer.parseInt(element.attributeValue("boosthreadnum"));
		if(element.attributeValue("workethreadnum") != null)
			this.workeThreadNum = Integer.parseInt(element.attributeValue("workethreadnum"));
		if(element.attributeValue("inprotolength") != null)
			this.inProtoLength = Integer.parseInt(element.attributeValue("inprotolength"));
		if(element.attributeValue("outprotolength") != null)
			this.outProtoLength  = Integer.parseInt(element.attributeValue("outprotolength"));
		if(element.attributeValue("port") != null)
			this.port = Integer.parseInt(element.attributeValue("port"));
		if(element.attributeValue("listener") != null) {
			Class<?> listenerClass = Class.forName(element.attributeValue("listener"));
			this.listener = (ServerListener) listenerClass.newInstance();
		}
		this.build();
	}
}
