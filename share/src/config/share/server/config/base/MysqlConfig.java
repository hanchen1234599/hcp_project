package share.server.config.base;

public class MysqlConfig extends BaseConfig {
	private int workeThreadNum = 1;
	public int getWorkeThreadNum() {
		return workeThreadNum;
	}
	public void setWorkeThreadNum(int workeThreadNum) {
		this.workeThreadNum = workeThreadNum;
	}
}
