package share.server.config.base;

import com.hc.component.net.server.ServerComponent;
import com.hc.component.net.server.ServerListener;

import share.Config;

public class ServerConfig extends BaseConfig {
	private int boosThreadNum = Config.Component.serverDefaultBoosThreadNum;
	private int workeThreadNum = Config.Component.serverDefaultWorkeThreadNum;
	private int inProtoLength = Config.Component.netProtoLength;
	private int outProtoLength = Config.Component.netProtoLength;
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
	public ServerComponent createServerComponent(int port) throws Exception {
		ServerComponent server = new ServerComponent();
		server.setEventLoop(this.getBoosThreadNum(), this.getWorkeThreadNum());
		server.setInProtoLength(this.getInProtoLength());
		server.setOutProtoLength(this.getOutProtoLength());
		server.setListener((ServerListener) Class.forName(this.getListener()).newInstance());
		server.setPort(port);
		return server;
	}
}
