package share.server.config.base;

import com.hc.component.db.mysql.MysqlComponent;
import com.hc.component.db.mysql.MysqlListener;

public class MysqlConfig extends BaseConfig {
	private int workeThreadNum = 1;
	public int getWorkeThreadNum() {
		return workeThreadNum;
	}
	public void setWorkeThreadNum(int workeThreadNum) {
		this.workeThreadNum = workeThreadNum;
	}
	public MysqlComponent createMysqlComponent( String hikariconfig ) throws Exception {
		MysqlComponent mysql = new MysqlComponent();
		mysql.setHikaricpConfigPaht(hikariconfig);
		mysql.setListener((MysqlListener) Class.forName(this.getListener()).newInstance());
		mysql.setUseThread(this.getWorkeThreadNum());
		return mysql;
	}
}
