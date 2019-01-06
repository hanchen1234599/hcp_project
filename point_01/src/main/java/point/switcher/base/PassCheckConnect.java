package point.switcher.base;

import com.hc.component.net.client.ClientListener;
import com.hc.component.net.client.ClientManager;
import com.hc.component.net.session.Session;
import com.hc.share.util.Trace;

import hc.server.service.Login;
import io.netty.buffer.ByteBuf;
import point.switcher.GateApp;
/**
 * @author hanchen
 * 连接登录服务
 */
public class PassCheckConnect implements ClientListener {
	private ClientManager manager = null; 
	private Session session = null;
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
		this.session = session;
		GateApp.getInstance().setLogin(new Login(session));
	}

	@Override
	public void onUnConnect() {
		this.session = null;
		GateApp.getInstance().setLogin(null);
	}

	@Override
	public void onConnectException() {
		this.session.getChannel().close();
	}

	@Override
	public void onData(Session session, ByteBuf body) {
		GateApp.getInstance().recvLoginProto( session, body );
	}
}
