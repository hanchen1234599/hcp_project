package login.base;

import com.hc.component.net.server.ServerListener;
import com.hc.component.net.server.ServerManager;
import com.hc.component.net.session.Session;
import com.hc.share.util.Trace;

import io.netty.buffer.ByteBuf;
import login.LoginApp;

public class LoginServerManager implements ServerListener {
	@Override
	public void onInit(ServerManager manager) {
		LoginApp.getInstace().setServer(manager);
		try {
			manager.open();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onDestory(ServerManager manager) {
		LoginApp.getInstace().setServer(null);
	}

	@Override
	public void onAddSession(Session session) {
		LoginApp.getInstace().addGateConnect(session);
	}

	@Override
	public void onRemoveSession(Session session) {
		LoginApp.getInstace().removeGateConnect(session);
	}

	@Override
	public void onData(Session session, ByteBuf body) {
		LoginApp.getInstace().recvGateProto(session, body);
	}

	@Override
	public void OnExceptionCaught(Session session, Throwable cause) {
		Trace.logger.info(cause);
	}
}
