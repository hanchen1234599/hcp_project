package point.login.base;

import com.hc.component.net.session.Session;

public interface LoginModule{
	void onAddGateConnect(Gate gate);
	void onRemoveGateConnect(Gate gate);
	void onProtoBuf(Session<byte[]> session, int pid, byte[] body);
	String getModuleName();
	void onLaunchLogin();
	void onDbComplate();
}
