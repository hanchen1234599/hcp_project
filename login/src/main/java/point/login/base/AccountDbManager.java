package point.login.base;

import com.hc.component.db.mysql.MysqlListener;
import com.hc.component.db.mysql.MysqlManager;
import com.hc.share.util.Trace;

import point.login.LoginApp;

public class AccountDbManager implements MysqlListener {
	@Override
	public void onInit(MysqlManager manager) {
		Trace.logger.info("AccountDbManager onInit");
		try {
			manager.open();//数据库准备好了
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		LoginApp.getInstace().setDb(manager);
	}

	@Override
	public void onDestory(MysqlManager manager) {
		LoginApp.getInstace().setDb(null);
	}
}
