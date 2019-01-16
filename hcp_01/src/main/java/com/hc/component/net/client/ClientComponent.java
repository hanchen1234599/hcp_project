package com.hc.component.net.client;

import com.hc.component.net.client.base.ClientManagerImpl;
import com.hc.share.Component;

public class ClientComponent extends Component<ClientManager, ClientListener> implements ClientBuilder {
	private int workeThreadNum = 4;
	private int inProtoLength = 64 * 1024;
	private int outProtoLength  = 64 * 1024;
	private int port = 0;
	private String host = null;

	@Override
	public ClientBuilder setConnect(String host, int port) {
		this.host = host;
		this.port = port;
		return this;
	}

	@Override
	public ClientBuilder setEventLoop(int workeThreadNum) {
		this.workeThreadNum = workeThreadNum;
		return this;
	}

	@Override
	public ClientBuilder setInProtoLength(int maxFrameLength) {
		this.inProtoLength = maxFrameLength;
		return this;
	}

	@Override
	public ClientBuilder setOutProtoLength(int maxFrameLength) {
		this.outProtoLength = maxFrameLength;
		return this;
	}
	
	@Override
	public void build() throws Exception {
		//this.setType(ComponentType.CLIENT);
		this.manager = new ClientManagerImpl(host, port, workeThreadNum, inProtoLength, outProtoLength);
		this.manager.registListener(listener);
		this.listener.onInit(this.manager);
	}
	
//	@Override
//	public void build(Element element) throws Exception {
//		if(element.attributeValue("workeThreadNum") != null)
//			this.workeThreadNum = Integer.parseInt(element.attributeValue("workethreadnum"));
//		if(element.attributeValue("inprotolength") != null)
//			this.inProtoLength = Integer.parseInt(element.attributeValue("inprotolength"));
//		if(element.attributeValue("outprotolength") != null)
//			this.outProtoLength  = Integer.parseInt(element.attributeValue("outprotolength"));
//		if(element.attributeValue("port") != null)
//			this.port = Integer.parseInt(element.attributeValue("port"));
//		if(element.attributeValue("host") != null)
//			this.host = element.attributeValue("host");
//		if(element.attributeValue("listener") != null) {
//			Class<?> listenerClass = Class.forName(element.attributeValue("listener"));
//			this.listener = (ClientListener) listenerClass.newInstance();
//		}
//		this.build();
//	}
}
