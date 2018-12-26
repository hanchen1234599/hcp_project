package com.hc.test;

import com.hc.component.net.client.ClientComponent;
import com.hc.component.net.client.ClientListener;
import com.hc.component.net.client.ClientManager;
import com.hc.component.net.session.Session;
import com.hc.share.util.Trace;

import io.netty.buffer.ByteBuf;

public class Main1 {

	public static void main(String[] args) {
//		// TODO Auto-generated method stub
//		ByteBuf buf = Unpooled.copyInt(100);
//		byte[] intbuf = new byte[4];
//		buf.readBytes(intbuf, 0, 4);
//		ByteBuf buf1 = Unpooled.wrappedBuffer(intbuf);
//		Trace.info(buf1.readInt());
		ClientComponent component = new ClientComponent();
		component.setConnect("127.0.0.1", 8899).setEventLoop(2).setInProtoLength(64 * 1024).setInProtoLength(64 * 1024);
		component.setListener(new myClientListener());
		try {
			component.build();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			Thread.sleep(1000000000);
			Trace.info("client end");
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}

class myClientListener implements ClientListener{

	@Override
	public void onInit(ClientManager manager) {
		// TODO Auto-generated method stub
		Trace.info("client manager init");
		try {
			manager.open();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void onDestory(ClientManager manager) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onConnect(Session session) {
		// TODO Auto-generated method stub
		Trace.info("client manager onConnect");
//		String str = "hanchen";
//		try {
			//byte[] body = str.getBytes("utf-8");
			//ByteBuf buf = Unpooled.wrappedBuffer(body);
			//session.send(body);
//		} catch (UnsupportedEncodingException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	}

	@Override
	public void onUnConnect() {
		// TODO Auto-generated method stub
		Trace.info("client manager onUnConnect");
	}

	@Override
	public void onConnectException() {
		// TODO Auto-generated method stub
		Trace.info("client manager onConnectException");
	}

	@Override
	public void onData(Session session, ByteBuf body) {
		// TODO Auto-generated method stub
		
	}
}
