package com.hc.component.net.websocket;

import org.dom4j.Element;

import com.hc.component.Type;
import com.hc.component.net.websocket.base.WebSocketManagerImpl;
import com.hc.share.Component;

public class WebSocketComponent extends Component<WebSocketManager, WebSocketListener> implements WebSocketBuilder {
	private int boosThreadNum = 1;
	private int workeThreadNum = 4;
	private int port = 0;
	
	@Override
	public void setListener(WebSocketListener listener) {
		this.listener = listener;
	}

	@Override
	public WebSocketBuilder setPort(int port) {
		this.port = port;
		return this;
	}

	@Override
	public WebSocketBuilder setEventLoop(int boosThreadNum, int workeThreadNum) {
		this.boosThreadNum = boosThreadNum;
		this.workeThreadNum = workeThreadNum;
		return this;
	}
	@Override
	public void build() throws Exception {
		this.setType(Type.WEBSOCKT);	
		this.manager = new WebSocketManagerImpl(this.port, this.boosThreadNum, this.workeThreadNum);
		this.manager.registListener(this.listener);
		this.listener.onInit(this.manager);
	}
	
	@Override
	public void build(Element element) throws Exception {
		if(element.attributeValue("boosthreadnum") != null)
			this.boosThreadNum = Integer.parseInt(element.attributeValue("boosthreadnum"));
		if(element.attributeValue("workethreadnum") != null)
			this.workeThreadNum = Integer.parseInt(element.attributeValue("workethreadnum"));
		if(element.attributeValue("port") != null)
			this.port = Integer.parseInt(element.attributeValue("port"));
		if(element.attributeValue("listener") != null) {
			Class<?> listenerClass = Class.forName(element.attributeValue("listener"));
			this.listener = (WebSocketListener) listenerClass.newInstance();
		}
		this.build();
	}
}
