package com.hc.share.util;

import java.security.MessageDigest;
import java.util.Base64;
import java.util.UUID;

public class Utils {
	public static String encodeMd5TwoBase64(String str) throws Exception{
		return Base64.getEncoder().encodeToString(MessageDigest.getInstance("MD5").digest(str.getBytes("utf-8")));
	}
	public static String randomString() {
		return UUID.randomUUID().toString().replaceAll("-","");
	}
}
