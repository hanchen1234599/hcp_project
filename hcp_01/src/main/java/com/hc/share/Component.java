package com.hc.share;

//import com.hc.component.ComponentType;

@SuppressWarnings("rawtypes")
public abstract class Component<T extends Manager, E extends Listener> {
	protected E listener;
	protected T manager;
	
	public T getManager() {
		return manager;
	}

	public void setManager(T manager) {
		this.manager = manager;
	}

	public void setListener(E listener) {
		this.listener = listener;
	}

	public E getListener() {
		return listener;
	}
}
