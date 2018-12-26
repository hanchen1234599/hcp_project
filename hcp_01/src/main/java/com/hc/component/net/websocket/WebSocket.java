package com.hc.component.net.websocket;

import java.io.UnsupportedEncodingException;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.CharsetUtil;

public class WebSocket {
	/**
	 * 回复http响应 关闭链接
	 * @param ctx
	 * @param req
	 * @param state 响应成功  还是失败
	 */
	public static void sendHttpResponse(Channel chanel, boolean state, String body ) {
		if (chanel == null || chanel.isActive() == false) return;
		FullHttpResponse res = null;
		if(state == true) {
			res = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
			res.headers().set("Content-Type", "text/plain");
			res.headers().set("charset", "UTF-8");
			res.headers().set("Connection", HttpHeaderValues.KEEP_ALIVE);
			try {
				res.content().writeBytes(body.getBytes("utf-8"));
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}else {
			res = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.BAD_REQUEST);
		}
					
		if (res.status().code() != 200) {
			ByteBuf buf = Unpooled.copiedBuffer(res.status().toString(), CharsetUtil.UTF_8);
			res.content().writeBytes(buf);
			buf.release();
		}
		ChannelFuture f = chanel.writeAndFlush(res);
		f.addListener(ChannelFutureListener.CLOSE); //http链接 在响应后关闭
	}
	public static void sendTextWebSocketFrame(Channel channel, ByteBuf body) {
		if(channel == null || channel.isActive() == false)
			return;
		channel.writeAndFlush(new TextWebSocketFrame(body));
	}
}
