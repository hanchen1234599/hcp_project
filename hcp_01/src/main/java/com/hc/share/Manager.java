package com.hc.share;
@SuppressWarnings("rawtypes")
public interface Manager<T extends Listener> {
	void registListener(T listener);
	void open() throws Exception;
	void close();
}
