package com.hc.component.net.server.base;

import com.hc.component.net.session.Decode;
import com.hc.component.net.session.Encode;
import com.hc.component.net.session.SessionContainer;
import com.hc.share.util.Trace;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public interface NettyServer {
	void openListen(SessionContainer container);
	void close();
	static class NettyServerImpl implements NettyServer{
		private EventLoopGroup boss;
		private EventLoopGroup work;
		int inProtoLength = 1024 * 64;
		int outProtoLength = 1024 * 64;
		private int port;
		public void setPort(int port) {
			this.port = port;
		}
		public void setOutProtoLength(int outProtoLength) {
			this.outProtoLength = outProtoLength;
		}
		public void setInProtoLength(int inProtoLength) {
			this.inProtoLength = inProtoLength;
		}
		public void setBossThreadNum(int boosThreadNum) {
			this.boss = new NioEventLoopGroup(boosThreadNum);
		}
		public void setWorkThreadNum(int workeThreadNum) {
			this.work = new NioEventLoopGroup(workeThreadNum);
		}
		//.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 3000)
		@Override
		public void openListen(SessionContainer container) {
			ServerBootstrap bs = new ServerBootstrap();
			bs.group(this.boss, this.work).option(ChannelOption.SO_BACKLOG, 32)
			.channel(NioServerSocketChannel.class).childHandler(new ChannelInitializer<SocketChannel>() {
				@Override
				protected void initChannel(SocketChannel ch) throws Exception {
					ch.pipeline().addLast("decode", new Decode(inProtoLength));
					ch.pipeline().addLast("encode", new Encode(outProtoLength));
					ch.pipeline().addLast("logic", new ServerLogic(container));
				}
			});
			try {
				ChannelFuture future = bs.bind(port).sync();
				future.addListener((f)->{
					if(f.isSuccess()) {
						Trace.logger.info("open listener port:" + port + " success");
					}else {
						Trace.logger.info("open listener port:" + port + " error");
						Runtime.getRuntime().exit(1);
					}
				});	
			} catch (InterruptedException e) {
				Trace.logger.error(e);
			}
			
		}

		@Override
		public void close() {
			this.boss.shutdownGracefully();
			this.work.shutdownGracefully();
		}
	}
}


