package share.server.config.base;

import com.hc.component.net.client.ClientComponent;
import com.hc.component.net.client.ClientListener;

import share.Config;

public class ClientConfig extends BaseConfig {
	private int workeThreadNum = Config.Component.serverDefaultBoosThreadNum;
	private int inProtoLength = Config.Component.netProtoLength;
	private int outProtoLength = Config.Component.netProtoLength;

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
	public ClientComponent createClientComponent(String remoteIp, int remotePort) throws Exception {
		ClientComponent client = new ClientComponent();
		client.setEventLoop(this.getWorkeThreadNum());
		client.setInProtoLength(this.getInProtoLength());
		client.setOutProtoLength(this.getOutProtoLength());
		client.setListener((ClientListener) this.getListener());
		client.setConnect(remoteIp, remotePort);
		return client;
	}
}
