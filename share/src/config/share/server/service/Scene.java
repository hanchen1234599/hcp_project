package share.server.service;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.hc.component.net.session.Session;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Scene extends Server {
	public Scene(Session session, int serverID) {
		super(session, serverID);
	}
}
