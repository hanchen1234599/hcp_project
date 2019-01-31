package share.server.config.base;

import com.hc.component.db.mysql.MysqlComponent;
import com.hc.component.db.mysql.MysqlListener;

import share.Config;

public class MysqlConfig extends BaseConfig {
	private int workeThreadNum = Config.Component.mysqlDefaultThreadNum;
	public int getWorkeThreadNum() {
		return workeThreadNum;
	}
	public void setWorkeThreadNum(int workeThreadNum) {
		this.workeThreadNum = workeThreadNum;
	}
	public MysqlComponent createMysqlComponent( String hikariconfig ) throws Exception {
		MysqlComponent mysql = new MysqlComponent();
		mysql.setHikaricpConfigPaht(hikariconfig);
		mysql.setListener((MysqlListener) this.getListener());
		mysql.setUseThread(this.getWorkeThreadNum());
		return mysql;
	}
}
