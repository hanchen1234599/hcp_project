package com.hc.test;

import java.io.UnsupportedEncodingException;

import com.hc.component.net.session.Session;
import com.hc.component.net.websocket.WebSocketComponent;
import com.hc.component.net.websocket.WebSocketListener;
import com.hc.component.net.websocket.WebSocketManager;
import com.hc.share.util.Trace;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderValues;

public class TestWebsocket {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		WebSocketComponent componet = new WebSocketComponent();
		componet.setEventLoop(1, 2).setPort(7777);
		componet.setListener(new MyListener1());
		try {
			componet.build();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Trace.info("server begin");
		try {
			Thread.sleep(100000000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
class MyListener1 implements WebSocketListener{

	@Override
	public void onInit(WebSocketManager manager) {
		// TODO Auto-generated method stub
		try {
			manager.open();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void onDestory(WebSocketManager manager) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onAddSession(Session session) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onRemoveSession(Session session) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onExceptionSession(Session session) {
		// TODO Auto-generated method stub
		session.getChannel().close();
	}

	@Override
	public void onData(Session session, ByteBuf body) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onHttp(Session session, FullHttpRequest req, FullHttpResponse rsp) {
		// TODO Auto-generated method stub
		Trace.info(req);
		rsp.headers().set("Content-Type", "text/plain");
		rsp.headers().set("charset", "UTF-8");
		rsp.headers().set("Connection", HttpHeaderValues.KEEP_ALIVE);
		try {
			rsp.content().writeBytes("success".getBytes("utf-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		//ChannelFuture f = session.getChannel().writeAndFlush(rsp);
		//f.addListener(ChannelFutureListener.CLOSE); //http链接 在响应后关闭
	}
	
}
