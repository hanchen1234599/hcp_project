package login.base;

import com.hc.component.net.session.Session;

import io.netty.buffer.ByteBuf;
import share.server.service.Gate;

public interface LoginModule{
	void onAddGateConnect(Gate gate);
	void onRemoveGateConnect(Gate gate);
	void onGateProto(Session session, int pid, ByteBuf body);
	String getModuleName();
	void onLaunchLogin();
	void onDbComplate();
}
