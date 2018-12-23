package point.login.logic.account;

import com.hc.component.net.session.Session;
import com.hc.share.util.Trace;

import point.login.LoginApp;
import point.login.logic.LogicAbstract;

public class AccountManager extends LogicAbstract {
	private static AccountManager instance = new AccountManager();
	private String moduleName = "account";
	private AccountManager() {}
	public static AccountManager getInstance() {
		return instance;
	} 
	
	@Override
	public void onLaunchLogin() {
		Trace.logger.info("onLauchLogin");
		// 预留 11~20 协议为账号协议
		LoginApp.getInstace().registerProtoBufProtoProtocol(11, this);
	}
	@Override
	public void onDbComplate() {
		Trace.logger.info("on db complete");
	}

	@Override
	public String getModuleName() {
		return this.moduleName;
	}
	@Override
	public void onProtoBuf(Session<byte[]> session, int pid, byte[] body) {
		Trace.logger.info("recv protobuf");
	}
}
