package com.hc.share;

import com.hc.component.ComponentType;

@SuppressWarnings("rawtypes")
public abstract class Component<T extends Manager, E extends Listener> {
	protected E listener;
	protected T manager;
	private ComponentType type;
	public ComponentType getType() {
		return type;
	}
	public void setType(ComponentType type) {
		this.type = type;
	}
	public E getListener() {
		return listener;
	}
}
