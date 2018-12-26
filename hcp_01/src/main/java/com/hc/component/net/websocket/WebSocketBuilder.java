package com.hc.component.net.websocket;

import com.hc.share.Builder;

public interface WebSocketBuilder extends Builder<WebSocketListener>{
	WebSocketBuilder setPort(int port);
	WebSocketBuilder setEventLoop(int boosThreadNum, int workeThreadNum);
}
