package com.hc.share.exception;

public class NetException extends Exception {
	/**
	 *  网络模块异常
	 */
	private static final long serialVersionUID = 6001L;

	public NetException(String message) {
        super(message);
    }
}
