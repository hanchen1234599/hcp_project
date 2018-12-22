package point.login.base;

import com.hc.component.net.session.Session;
/**
 * @author hanchen
 * session 标记一个网关连接
 * isOpen 标记这个网关的连接是不是有效
 */
public class Gate {
	Session<byte[]> session = null;
	private volatile boolean isOpen = true; //todo暂时没有gate验证逻辑默认为true
	public Gate(Session<byte[]> session) {
		this.session = session;
	}
	public boolean isOpen() {
		return isOpen;
	}
	public void setOpen(boolean isOpen) {
		this.isOpen = isOpen;
	}
	public Session<byte[]> getSession() {
		return session;
	}
	public void setSession(Session<byte[]> session) {
		this.session = session;
	}
}
