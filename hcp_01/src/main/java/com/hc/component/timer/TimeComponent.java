package com.hc.component.timer;

public class TimeComponent {
	private int nThread = 1;
	
	public TimeComponent setNThread( int nThread) {
		this.nThread = nThread;
		return this;
	}
	public Timer build() {
		return new TimerImpl(this.nThread);
	}
}
