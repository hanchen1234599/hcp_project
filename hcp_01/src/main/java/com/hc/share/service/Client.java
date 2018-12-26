package com.hc.share.service;

import com.hc.component.net.session.Session;

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
