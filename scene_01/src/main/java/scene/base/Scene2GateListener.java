package scene.base;

import com.hc.component.net.client.ClientListener;
import com.hc.component.net.client.ClientManager;
import com.hc.component.net.session.Session;

import io.netty.buffer.ByteBuf;

public class Scene2GateListener implements ClientListener {

	@Override
	public void onInit(ClientManager manager) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onDestory(ClientManager manager) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onConnect(Session session) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onUnConnect(Session session) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onConnectException(Session session, Throwable cause) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onData(Session session, ByteBuf body) {
		// TODO Auto-generated method stub

	}

}
