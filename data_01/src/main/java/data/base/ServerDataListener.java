package data.base;

import com.hc.component.net.server.ServerListener;
import com.hc.component.net.server.ServerManager;
import com.hc.component.net.session.Session;

import io.netty.buffer.ByteBuf;

public class ServerDataListener implements ServerListener {

	@Override
	public void onInit(ServerManager manager) {

	}

	@Override
	public void onDestory(ServerManager manager) {

	}

	@Override
	public void onAddSession(Session session) {

	}

	@Override
	public void onRemoveSession(Session session) {

	}

	@Override
	public void onData(Session session, ByteBuf body) {

	}

	@Override
	public void OnExceptionCaught(Session session, Throwable cause) {

	}

}
