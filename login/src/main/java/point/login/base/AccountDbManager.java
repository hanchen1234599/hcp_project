package point.login.base;

import com.hc.component.db.mysql.MysqlListener;
import com.hc.component.db.mysql.MysqlManager;
import com.hc.share.util.Trace;

import point.login.logic.LoginApp;

public class AccountDbManager implements MysqlListener {
	@Override
	public void onInit(MysqlManager manager) {
		LoginApp.getInstace().setDb(manager);
		try {
			manager.open();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Trace.logger.info("AccountDbManager onInit");
	}

	@Override
	public void onDestory(MysqlManager manager) {
		LoginApp.getInstace().setDb(null);
	}
}
