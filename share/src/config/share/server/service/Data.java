package share.server.service;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.hc.component.net.session.Session;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Data extends Server {
	private Integer innerPort = 0;
	public Data(Session session, int serverID) {
		super(session, serverID);
	}
	public Integer getInnerPort() {
		return innerPort;
	}
	public void setInnerPort(Integer innerPort) {
		this.innerPort = innerPort;
	}

}
