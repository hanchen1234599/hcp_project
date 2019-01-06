package point.login.base;

import com.hc.component.net.session.Session;

import hc.server.service.Gate;
import io.netty.buffer.ByteBuf;

public interface LoginModule{
	void onAddGateConnect(Gate gate);
	void onRemoveGateConnect(Gate gate);
	void onGateProto(Session session, int pid, ByteBuf body);
	String getModuleName();
	void onLaunchLogin();
	void onDbComplate();
}
