package com.hc.share.util;

import java.util.concurrent.atomic.AtomicLong;

public class AtomicSessionID {
	private static AtomicLong atomic = new AtomicLong(100);
	public static long getOnlySessionID() {
		return atomic.getAndIncrement();
	}
}
