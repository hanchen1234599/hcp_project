package center.base;

import com.hc.component.net.server.ServerListener;
import com.hc.component.net.server.ServerManager;
import com.hc.component.net.session.Session;
import com.hc.share.util.Trace;

import center.CenterApp;
import io.netty.buffer.ByteBuf;

public class ServerCenterListener implements ServerListener {
	
	@Override
	public void onInit(ServerManager manager) {
		try {
			manager.open();
			CenterApp.getInstance().setNetManager(manager);
		} catch (Exception e) {
			Trace.logger.error(e);
			Runtime.getRuntime().exit(1);
		}
	}

	@Override
	public void onDestory(ServerManager manager) {
		CenterApp.getInstance().setNetManager(null);
	}

	@Override
	public void onData(Session session, ByteBuf body) {

	}

	@Override
	public void OnExceptionCaught(Session session, Throwable cause) {
		
	}

	@Override
	public void onAddSession(Session session) {
		
	}

	@Override
	public void onRemoveSession(Session session) {
		
	}
}
