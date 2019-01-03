package serverType;

public class MysqlConfig {
	private String packagePath = "";
	private int workeThreadNum = 1;
	private String listener = "";
	public String getPackagePath() {
		return packagePath;
	}
	public void setPackagePath(String packagePath) {
		this.packagePath = packagePath;
	}
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
