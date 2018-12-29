package com.hc.test;

import com.hc.share.util.Trace;

public class Test9 {
	public static void main(String args[]) {
		int length = args.length;
		Trace.logger.info(length);
		Trace.logger.info(args[0]);
	}
}
