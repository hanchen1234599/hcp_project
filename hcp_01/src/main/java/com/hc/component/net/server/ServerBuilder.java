package com.hc.component.net.server;

import com.hc.share.Builder;

public interface ServerBuilder extends Builder<ServerListener>{
	ServerBuilder setPort(int port);
	ServerBuilder setEventLoop(int boosThreadNum, int workeThreadNum);
	ServerBuilder setInProtoLength(int maxFrameLength);
	ServerBuilder setOutProtoLength(int maxFrameLength);
}
