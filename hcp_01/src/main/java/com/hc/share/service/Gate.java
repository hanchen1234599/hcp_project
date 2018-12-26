package com.hc.share.service;

import com.hc.component.net.session.Session;
/**
 * @author hanchen
 * session 标记一个网关连接
 * isOpen 标记这个网关的连接是不是有效
 */
public class Gate extends Server {
	public Gate(Session session, int serverID) {
		super(session, serverID);
	}
	public Gate(Session session) {
		super(session, 0);
	}
}
