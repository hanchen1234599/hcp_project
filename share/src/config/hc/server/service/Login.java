package hc.server.service;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.hc.component.net.session.Session;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Login{
	private Session session = null;
	private volatile boolean isOpen = true; //todo暂时没有gate验证逻辑默认为true
	public Login(Session session) {
		this.session = session; 
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
}
