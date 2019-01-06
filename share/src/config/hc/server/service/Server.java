package hc.server.service;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.hc.component.net.session.Session;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Server {
	private Integer serverId;
	private String serverName = "";
	@JsonIgnore
	private Session session = null;
	private volatile boolean isOpen = true; // todo暂时没有gate验证逻辑默认为true

	public Server(Session session, int serverID) {
		this.session = session;
		this.serverId = serverID;
	}

	public int getServerId() {
		return serverId;
	}

	public void setServerId(int serverId) {
		this.serverId = serverId;
	}

	public String getServerName() {
		return serverName;
	}

	public void setServerName(String serverName) {
		this.serverName = serverName;
	}

	public Session getSession() {
		return session;
	}

	public void setSession(Session session) {
		this.session = session;
	}

	public boolean isOpen() {
		return isOpen;
	}

	public void setOpen(boolean isOpen) {
		this.isOpen = isOpen;
	}

}
