package share.server.config.base;

public class ServerConfig extends BaseConfig {
	private int boosThreadNum = 1;
	private int workeThreadNum = 4;
	private int inProtoLength = 65536;
	private int outProtoLength = 65536;
	public int getBoosThreadNum() {
		return boosThreadNum;
	}
	public void setBoosThreadNum(int boosThreadNum) {
		this.boosThreadNum = boosThreadNum;
	}
	public int getWorkeThreadNum() {
		return workeThreadNum;
	}
	public void setWorkeThreadNum(int workeThreadNum) {
		this.workeThreadNum = workeThreadNum;
	}
	public int getInProtoLength() {
		return inProtoLength;
	}
	public void setInProtoLength(int inProtoLength) {
		this.inProtoLength = inProtoLength;
	}
	public int getOutProtoLength() {
		return outProtoLength;
	}
	public void setOutProtoLength(int outProtoLength) {
		this.outProtoLength = outProtoLength;
	}
}