package point.login.base;

import com.hc.component.net.session.Session;
import com.hc.share.service.Gate;

public interface LoginModule{
	void onAddGateConnect(Gate gate);
	void onRemoveGateConnect(Gate gate);
	void onGateProto(Session session, int pid, byte[] body);
	String getModuleName();
	void onLaunchLogin();
	void onDbComplate();
}
