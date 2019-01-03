package center.base;

import com.hc.component.db.mysql.MysqlListener;
import com.hc.component.db.mysql.MysqlManager;
import com.hc.share.util.Trace;

import center.CenterApp;

public class DbCenterListener implements MysqlListener {
	@Override
	public void onInit(MysqlManager manager) {
		try {
			manager.open();
			CenterApp.getInstance().setDbManager(manager);
		} catch (Exception e) {
			Trace.logger.error(e);
		}
	}

	@Override
	public void onDestory(MysqlManager manager) {
		CenterApp.getInstance().setDbManager(null);
	}
}
