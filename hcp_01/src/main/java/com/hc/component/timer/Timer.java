package com.hc.component.timer;

import java.util.concurrent.ExecutorService;
public interface Timer {
	void registerOnceTask( String name, Runnable run, long milliseSecond );
	void registerOnceTask( String name, Runnable run, long milliseSecond, ExecutorService exec );
	void registerLoopTask( String name, Runnable run, long milliseSecond);
	void registerLoopTask( String name, Runnable run, long milliseSecond, ExecutorService exec);
	void cancleTask( String name);
}
