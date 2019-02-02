package com.hc.component.net.websocket.base;

import java.util.concurrent.ConcurrentHashMap;
import com.hc.component.net.session.Session;
import com.hc.component.net.websocket.WebSocket;
import com.hc.share.util.AtomicSessionID;
import com.hc.share.util.Trace;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import io.netty.handler.codec.http.websocketx.ContinuationWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PingWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PongWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshaker;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshakerFactory;
import io.netty.handler.stream.ChunkedWriteHandler;

public class WebSocketServer {
	private EventLoopGroup boss;
	private EventLoopGroup work;
	private int port = 0;

	public WebSocketServer(int port, int boosThreadNum, int workeThreadNum) {
		this.boss = new NioEventLoopGroup(boosThreadNum);
		this.work = new NioEventLoopGroup(workeThreadNum);
		this.port = port;
	}

	/**
	 * option(ChannelOption.SO_BACKLOG, 32) option(ChannelOption.SO_KEEPALIVE, true)
	 * private SslContext ssl = SslContext.newServerContext(certChainFile, keyFile)
	 * SSLEngine engine = ssl.newEngine(ch.alloc());
	 * .option(ChannelOption.SO_KEEPALIVE, true)
	 * ch.pipeline().addLast("compressor",new HttpContentCompressor());
	 */
	public void openListen(WebSocketManagerImpl manager) {
		ServerBootstrap bs = new ServerBootstrap();
		bs.group(this.boss, this.work).option(ChannelOption.SO_BACKLOG, 32)
				.channel(NioServerSocketChannel.class).childHandler(new ChannelInitializer<SocketChannel>() {
					@Override
					protected void initChannel(SocketChannel ch) throws Exception {
						ch.pipeline().addLast("codec", new HttpServerCodec());
						ch.pipeline().addLast("aggegator", new HttpObjectAggregator(64 * 1024));
						ch.pipeline().addLast("http-chunked", new ChunkedWriteHandler());
						ch.pipeline().addLast("websocket", new WebSocketLogic(manager));
					}
				});
		try {
			ChannelFuture future = bs.bind(port).sync();
			future.addListener((f)->{
				if(f.isSuccess()) {
					Trace.logger.info("open listener port:" + port + " success");
				}else {
					Trace.logger.error("open listener port:" + port + " error");
					this.close();
					Runtime.getRuntime().exit(1);
				}
			});	
		} catch (InterruptedException e) {
			Trace.logger.error(e);
		}
	}

	public void close() {
		this.boss.shutdownGracefully();
		this.work.shutdownGracefully();
	}

	/**
	 * @author hanchen BinaryWebSocketFrame，包含二进制数据 TextWebSocketFrame，包含文本数据
	 *         ContinuationWebSocketFrame，包含二进制数据或文本数据，BinaryWebSocketFrame和TextWebSocketFrame的结合体
	 *         CloseWebSocketFrame，WebSocketFrame代表一个关闭请求，包含关闭状态码和短语
	 *         CloseWebSocketFrame 关闭请求 PingWebSocketFrame PongWebSocketFrame
	 */
	public static final class WebSocketLogic extends ChannelInboundHandlerAdapter {
		private WebSocketManagerImpl manager;
		private ConcurrentHashMap<Channel, WebSocketServerHandshaker> handshakerMap = new ConcurrentHashMap<>();

		public WebSocketLogic(WebSocketManagerImpl manager) {
			this.manager = manager;
		}

		@Override
		public void channelActive(ChannelHandlerContext ctx) throws Exception {
			long sessionID = AtomicSessionID.getOnlySessionID();
			Session session = new WebSocketSessionImpl(ctx.channel(), sessionID);
			this.manager.addSession(session);
		}

		@Override
		public void channelInactive(ChannelHandlerContext ctx) throws Exception {
			this.handshakerMap.remove(ctx.channel());
			this.manager.removeSession(ctx.channel());
		}

		@Override
		public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
			if (msg == null)
				return;
			if (msg instanceof FullHttpRequest) {
				handleHttpRequest(ctx, (FullHttpRequest) msg);
			} else if (msg instanceof WebSocketFrame) {
				handlerWebSocketFrame(ctx, (WebSocketFrame) msg);
			}
		}

		@Override
		public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
			Session session = this.manager.getSession(ctx.channel());
			this.manager.OnExceptionCaught(session, cause);
		}

		/**
		 * ctx.writeAndFlush(new TextWebSocketFrame( new SimpleDateFormat("yyyy-MM-dd
		 * HH:mm:ss").format(new Date()) + ": " + request));
		 * 
		 * @param ctx
		 * @param frame
		 */
		public void handlerWebSocketFrame(ChannelHandlerContext ctx, WebSocketFrame frame) {
			Session session = this.manager.getSession(ctx.channel());
			if (session == null)
				ctx.close();
			if (frame instanceof CloseWebSocketFrame) {
				handshakerMap.get(ctx.channel()).close(ctx.channel(), (CloseWebSocketFrame) frame.retain());
				return;
			}
			if (frame instanceof PingWebSocketFrame) {
				ctx.channel().write(new PongWebSocketFrame(frame.content().retain()));
				return;
			}
			if (frame instanceof TextWebSocketFrame) {//文本数据。
				this.manager.recvData(session, ((TextWebSocketFrame) frame).content());
			}
			if (frame instanceof BinaryWebSocketFrame) {//二进制数据。 未支持
				//this.manager.recvData(session, ((BinaryWebSocketFrame) frame).content());
				ctx.close();
			}
			if (frame instanceof ContinuationWebSocketFrame) {//ContinuationWebSocketFrame
				//this.manager.recvData(session, ((ContinuationWebSocketFrame) frame).content());
				ctx.close();
			}
		}

		/**
		 * 功能: 1建立websocket链接 2 处理http请求
		 * 
		 * @param ctx
		 * @param req
		 */
		public void handleHttpRequest(ChannelHandlerContext ctx, FullHttpRequest req) {
			if (req.decoderResult().isSuccess() != true) {
				WebSocket.sendHttpResponse(ctx.channel(), false, "");
				return;
			}
			if (req.headers().get("Upgrade") != null && req.headers().get("Upgrade").equals("websocket")) {
				WebSocketServerHandshaker handshaker = new WebSocketServerHandshakerFactory(
						"ws://" + req.headers().get("Host"), null, false).newHandshaker(req);
				handshakerMap.put(ctx.channel(), handshaker);
				handshaker.handshake(ctx.channel(), req);
			} else {
				Session session = this.manager.getSession(ctx.channel());
				if (session == null)
					ctx.close();
				req.headers().set("Content-Type", "text/plain");
				req.headers().set("charset", "UTF-8");
				req.headers().set("Connection", HttpHeaderValues.KEEP_ALIVE);
				this.manager.recvHttp(session, req,
						new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK));
			}
		}
	}
}