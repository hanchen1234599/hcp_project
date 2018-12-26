package com.hc.share;

import com.hc.component.Type;
import com.hc.component.db.mysql.MysqlComponent;
import com.hc.component.net.client.ClientComponent;
import com.hc.component.net.server.ServerComponent;
import com.hc.component.net.websocket.WebSocketComponent;

@SuppressWarnings("rawtypes")
public abstract class Component<T extends Manager, E extends Listener> {
	protected E listener;
	protected T manager;
	private Type type;
	public static Builder createComponent( String type ) {
		if(type.equals("client") )
			return new ClientComponent();
		if(type.equals("server"))
			return new ServerComponent();
		if(type.equals("websocket"))
			return new WebSocketComponent();
		if(type.equals("mysql"))
			return new MysqlComponent();
		return null;
	}
	public Type getType() {
		return type;
	}
	public void setType(Type type) {
		this.type = type;
	}
}
