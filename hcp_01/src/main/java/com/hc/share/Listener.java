package com.hc.share;
@SuppressWarnings("rawtypes")
public interface Listener<T extends Manager> {
	void onInit(T manager);
	void onDestory(T manager);
}
