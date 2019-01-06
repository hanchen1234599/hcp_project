package hc.server.service;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.hc.component.net.session.Session;
/**
 * @author hanchen
 * session 标记一个网关连接
 * isOpen 标记这个网关的连接是不是有效
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Gate extends Server {
	private Integer innerPort = 0;
	public Gate(Session session, int serverID) {
		super(session, serverID);
	}
	public Gate(Session session) {
		super(session, 0);
	}
	public Integer getInnerPort() {
		return innerPort;
	}
	public void setInnerPort(Integer innerPort) {
		this.innerPort = innerPort;
	}
}
