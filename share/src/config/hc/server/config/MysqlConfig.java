package hc.server.config;

public class MysqlConfig {
	private int workeThreadNum = 1;
	private String listener = "";
	public int getWorkeThreadNum() {
		return workeThreadNum;
	}
	public void setWorkeThreadNum(int workeThreadNum) {
		this.workeThreadNum = workeThreadNum;
	}
	public String getListener() {
		return listener;
	}
	public void setListener(String listener) {
		this.listener = listener;
	}
}
