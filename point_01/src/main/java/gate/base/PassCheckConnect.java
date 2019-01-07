package gate.base;

import com.hc.component.net.client.ClientListener;
import com.hc.component.net.client.ClientManager;
import com.hc.component.net.session.Session;
import com.hc.share.util.Trace;

import gate.GateApp;
import io.netty.buffer.ByteBuf;
import share.server.service.Login;
/**
 * @author hanchen
 * 连接登录服务
 */
public class PassCheckConnect implements ClientListener {
	private ClientManager manager = null; 
	@Override
	public void onInit(ClientManager manager) {
		this.manager = manager;
		try {
			this.manager.open();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void onDestory(ClientManager manager) {
		this.manager = manager;
		Trace.logger.info("PassCheckConnect onDestory");
	}

	@Override
	public void onConnect(Session session) {
		GateApp.getInstance().setLogin(new Login(session));
	}

	@Override
	public void onUnConnect(Session session) {
		GateApp.getInstance().setLogin(null);
	}

	@Override
	public void onData(Session session, ByteBuf body) {
		GateApp.getInstance().recvLoginProto( session, body );
	}

	@Override
	public void onConnectException(Session session, Throwable cause) {
		Trace.logger.error("sessionid:" + session.getSessionID() + " errror");
		Trace.logger.error(cause);
	}
}
