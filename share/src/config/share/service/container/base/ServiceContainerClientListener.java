package share.service.container.base;

import com.hc.component.net.client.ClientListener;
import com.hc.component.net.client.ClientManager;
import com.hc.component.net.session.Session;

import io.netty.buffer.ByteBuf;

public class ServiceContainerClientListener implements ClientListener {
	@Override
	public void onInit(ClientManager manager) {

	}

	@Override
	public void onDestory(ClientManager manager) {

	}

	@Override
	public void onConnect(Session session) {

	}

	@Override
	public void onUnConnect(Session session) {

	}

	@Override
	public void onConnectException(Session session, Throwable cause) {

	}

	@Override
	public void onData(Session session, ByteBuf body) {

	}
}
