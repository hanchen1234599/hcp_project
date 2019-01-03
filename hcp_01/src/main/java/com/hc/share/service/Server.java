package com.hc.share.service;

import com.hc.component.net.session.Session;

public class Server {
	private int serverId;
	private String serverName = "";
	private String jsonConfig = "";
	private Session session = null;
	private volatile boolean isOpen = true; //todo暂时没有gate验证逻辑默认为true
	public int getServerId() {
		return serverId;
	}
	public void setServerId(int serverId) {
		this.serverId = serverId;
	}
	public Server(Session session, int serverID) {
		this.session = session; 
		this.serverId = serverID;
	}
	public boolean isOpen() {
		return isOpen;
	}
	public void setOpen(boolean isOpen) {
		this.isOpen = isOpen;
	}
	public Session getSession() {
		return session;
	}
	public void setSession(Session session) {
		this.session = session;
	}
	public String getServerName() {
		return serverName;
	}
	public void setServerName(String serverName) {
		this.serverName = serverName;
	}
	public String getJsonConfig() {
		return jsonConfig;
	}
	public void setJsonConfig(String jsonConfig) {
		this.jsonConfig = jsonConfig;
	}
}
