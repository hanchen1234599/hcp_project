package login.base;

import com.hc.component.db.mysql.MysqlListener;
import com.hc.component.db.mysql.MysqlManager;

import login.LoginApp;

public class AccountDbManager implements MysqlListener {
	@Override
	public void onInit(MysqlManager manager) {
		try {
			manager.open();//数据库准备好了
		} catch (Exception e) {
			e.printStackTrace();
		}
		LoginApp.getInstace().setDb(manager);
	}

	@Override
	public void onDestory(MysqlManager manager) {
		LoginApp.getInstace().setDb(null);
	}
}
