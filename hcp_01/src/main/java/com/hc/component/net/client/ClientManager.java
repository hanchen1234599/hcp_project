package com.hc.component.net.client;

import com.hc.component.net.session.Session;
import com.hc.share.Manager;

public interface ClientManager extends Manager<ClientListener> {
	Session getSession();
}
