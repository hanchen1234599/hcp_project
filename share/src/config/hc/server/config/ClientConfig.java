package hc.server.config;

public class ClientConfig {
	private int workeThreadNum = 4;
	private int inProtoLength = 65536;
	private int outProtoLength = 65536;
	private String listener = "";

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

	public String getListener() {
		return listener;
	}

	public void setListener(String listener) {
		this.listener = listener;
	}
}
