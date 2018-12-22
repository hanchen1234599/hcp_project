package point.login.base;

import com.google.protobuf.Message.Builder;
import com.hc.component.net.session.Session;

public interface ModuleInterface {
	void onAddGateConnect(Gate gate);
	void onRemoveGateConnect(Gate gate);
	void onLaunchLogin();
	void onDbComplate();
	void onProtoBuf(Session<byte[]> session, Builder builder);
	String getModuleName();
}
