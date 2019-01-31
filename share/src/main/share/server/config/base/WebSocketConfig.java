package share.server.config.base;

import com.hc.component.net.websocket.WebSocketComponent;
import com.hc.component.net.websocket.WebSocketListener;

import share.Config;

public class WebSocketConfig extends BaseConfig {
	private int boosThreadNum = Config.Component.serverDefaultBoosThreadNum;
	private int workeThreadNum = Config.Component.serverDefaultWorkeThreadNum;
	public int getBoosThreadNum() {
		return boosThreadNum;
	}
	public void setBoosThreadNum(int boosThreadNum) {
		this.boosThreadNum = boosThreadNum;
	}
	public int getWorkeThreadNum() {
		return workeThreadNum;
	}
	public void setWorkeThreadNum(int workeThreadNum) {
		this.workeThreadNum = workeThreadNum;
	}
	public WebSocketComponent createWebSocket(int port) throws Exception {
		WebSocketComponent webSocket = new WebSocketComponent();
		webSocket.setEventLoop(boosThreadNum, workeThreadNum);
		webSocket.setListener((WebSocketListener) Class.forName(this.getListener()).newInstance());
		webSocket.setPort(port);
		return webSocket;
	}
}
