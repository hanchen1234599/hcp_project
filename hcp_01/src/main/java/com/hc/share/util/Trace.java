package com.hc.share.util;

import org.apache.log4j.Logger;

public class Trace {
	public static Logger logger = Logger.getLogger("log4j.properties");

	public static void info(Object log) {
		logger.info(log);
	}

	public static void debug(Object log) {
		logger.debug(log);
	}

	public static void error(Object log, Throwable t) {
		logger.error(log, t);
	}

	public static void error(Object log) {
		logger.error(log);
	}

	public static void warn(Object log) {
		logger.warn(log);
	}

	public static void fatal(Object log) {
		logger.fatal(log);
	}
}
