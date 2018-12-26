package point.switcher.base;

import com.hc.component.net.server.ServerListener;
import com.hc.component.net.server.ServerManager;
import com.hc.component.net.session.Session;
import com.hc.share.util.Trace;

import io.netty.buffer.ByteBuf;
import point.switcher.GateApp;
/**
 * @author hanchen
 */
public class OutServerListener implements ServerListener {
	@Override
	public void onInit(ServerManager manager) {
		GateApp.getInstace().setOuterManager(manager);
		try {
			manager.open();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onDestory(ServerManager manager) {
		GateApp.getInstace().setOuterManager(null);
		Trace.logger.info("OutServerListener onDestory");
	}
	
	@Override
	public void onAddSession(Session session) {
		GateApp.getInstace().onClientInactive(session);
	}

	@Override
	public void onRemoveSession(Session session) {
		GateApp.getInstace().onClientUnactive(session.getSessionID());
	}

	@Override
	public void onExceptionSession(Session session) {
		session.getChannel().close();
	}

	@Override
	public void onData(Session session, ByteBuf body) {
		GateApp.getInstace().recvClientProto( session, body );
	}
}
