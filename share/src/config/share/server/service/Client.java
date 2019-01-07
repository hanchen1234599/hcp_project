package share.server.service;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.hc.component.net.session.Session;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Client {
	private Session session = null;
	public Client(Session session) {
		this.session = session;
	}
	public Session getSession() {
		return session;
	}
	public void setSession(Session session) {
		this.session = session;
	}
}
