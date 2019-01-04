package com.hc.component.net.client.base;

import java.util.concurrent.TimeUnit;
import com.hc.component.net.session.Decode;
import com.hc.component.net.session.Encode;
import com.hc.share.util.Trace;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

public class NettyClient {
	private EventLoopGroup worke = null;
	private int inProtoLength = 64 * 1024;
	private int outProtoLength = 64 * 1024;
	private ClientManagerImpl manager = null;
	private String host = "127.0.0.1";
	private int port = 0;
	
	public NettyClient(String host, int port, int workeThreadNum, int inProtoLength, int outProtoLength, ClientManagerImpl manager) {
		this.worke = new NioEventLoopGroup(workeThreadNum);
		this.host = host;
		this.port = port;
		this.manager = manager;
		this.inProtoLength = inProtoLength;
		this.outProtoLength = outProtoLength;
	}
	//CONNECT_TIMEOUT_MILLIS  参数 .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
	/**
	 * 这里要实现超时重连机制
	 * 还有搞懂三次握手的SO_BACKLOG 的原理
	 * ChannelFutureListener
	 */
	public void beginConnect() {
		Trace.logger.info("connect host:" + this.host + " port:" + this.port + " ......");
		Encode encode = new Encode(this.outProtoLength);
		Decode decode = new Decode(this.inProtoLength);
		ClientLogic logic = new ClientLogic(this.manager);
		Bootstrap bootstrap = new Bootstrap();
		bootstrap.group(this.worke).channel(NioSocketChannel.class).option(ChannelOption.SO_KEEPALIVE, true).remoteAddress(this.host, this.port).handler(new ChannelInitializer<SocketChannel>() {
			@Override
			protected void initChannel(SocketChannel ch) throws Exception {
				ch.pipeline().addLast("decode", decode);
				ch.pipeline().addLast("encode", encode);
				ch.pipeline().addLast("logic", logic);
			}
		});
		try {
			bootstrap.connect().sync().addListener((ChannelFuture f) -> {
				if (!f.isSuccess()) {
					f.channel().eventLoop().schedule(new Runnable() {
						@Override
						public void run() {
							Trace.logger.debug("reConnect");
							beginConnect();
						}
					}, 5L, TimeUnit.SECONDS); // or you can give up at some point by just doing nothing.
				}else {
					Trace.logger.info("connect host:"+ this.host + " port:" + this.port + " success");
				}
			});
		} catch (InterruptedException e) {
			Trace.logger.error(e);
		}
	}
	
	public void close() {
		this.worke.shutdownGracefully();
	}
	
}
