package com.hc.component.net.client;

import com.hc.share.Builder;

public interface ClientBuilder extends Builder<ClientListener> {
	ClientBuilder setConnect(String host, int port);
	ClientBuilder setEventLoop(int workeThreadNum);
	ClientBuilder setInProtoLength(int maxFrameLength);
	ClientBuilder setOutProtoLength(int maxFrameLength);
}
