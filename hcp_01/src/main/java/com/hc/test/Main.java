package com.hc.test;

import com.hc.component.net.server.ServerListener;
import com.hc.component.net.server.ServerManager;
import com.hc.component.net.session.Session;
import com.hc.share.util.Trace;

import io.netty.buffer.ByteBuf;

public class Main {

	public static void main(String[] args) {
//		// TODO Auto-generated method stub
//		BasicBSONObject obj = new BasicBSONObject();
//		obj.put("int", 1);
//		obj.put("long", 111111111111l);
//		obj.put("double", 1.1);
//		obj.put("string", "string");
//		BSONObject childrenObj1 = new BasicBSONObject();
//		BSONObject childrenObj2 = new BasicBSONObject();
//		childrenObj2.put("b", "b");
//		obj.put("children1", childrenObj1);
//		obj.put("children2", childrenObj2);
//		obj.put("children3", childrenObj2);
//		BsonArray array = new BsonArray();
//		array.add(new BsonInt32(1));
//		array.add(new BsonInt32(3));
//		array.add(new BsonInt32(4));
//		obj.put("array", array);
//		Trace.info(obj);
//		childrenObj2.put("c", "c");
//		Trace.info(obj);
		
		
//		ServerComponent com = new ServerComponent();
//		com.setEventLoop(1, 4).setInProtoLength(64 * 1024).setOutProtoLength(64 * 1024).setPort(8899);
//		com.setListener(new myListener());
//		
//		try {
//			com.build();
//		} catch (NetException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		
////		NettyClient client = new NettyClient("www.baidu.com", 888, 4, 1024 * 64, 1024 * 64);
////		client.beginConnect();
//		try {
//			Thread.sleep(20000000);
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	}

}

class myListener implements ServerListener{

	@Override
	public void onInit(ServerManager manager) {
		// TODO Auto-generated method stub
		Trace.info("------------------");
		try {
			manager.open();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void onDestory(ServerManager manager) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onAddSession(Session session) {
		// TODO Auto-generated method stub
		Trace.info("onAddSession" + session.getSessionID());
	}

	@Override
	public void onRemoveSession(Session session) {
		// TODO Auto-generated method stub
		
	}
	// body 包体 数据
	@Override
	public void onData(Session session, ByteBuf body) {
		
// TODO Auto-generated method stub
//		try {
//			String str = new String(body, 4, body.length - 4, "utf-8");
//			Trace.info("ondata :" + str);
//		} catch (UnsupportedEncodingException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
	}

	@Override
	public void OnExceptionCaught(Session session, Throwable cause) {
		// TODO Auto-generated method stub
		
	}
}
