package point.login.logic.account;

import com.google.protobuf.Message.Builder;
import com.hc.component.net.session.Session;

import point.login.logic.LogicAbstract;

public class AccountManager extends LogicAbstract {
	private static AccountManager instance = new AccountManager();
	private String moduleName = "account";
	
	public AccountManager getInstance() {
		return instance;
	} 
	@Override
	public void onLaunchLogin() {
		
	}
	@Override
	public void onDbComplate() {
		
	}

	@Override
	public String getModuleName() {
		return this.moduleName;
	}
//	@Override
//	public void onProtocol(Session session, Builder) {
//		// TODO Auto-generated method stub
//		
//	}
	@Override
	public void onProtoBuf(Session<byte[]> session, Builder builder) {
		
	}
}
