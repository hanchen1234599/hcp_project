package point.login.base;

import com.hc.component.net.server.ServerListener;
import com.hc.component.net.server.ServerManager;
import com.hc.component.net.session.Session;

import io.netty.buffer.ByteBuf;
import point.login.logic.LoginApp;

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
	public void onAddSession(Session<byte[]> session) {
		
	}

	@Override
	public void onRemoveSession(Session<byte[]> session) {
		
	}

	@Override
	public void onExceptionSession(Session<byte[]> session) {
		
	}

	@Override
	public void onData(Session<byte[]> session, ByteBuf body) {
		
	}

}
