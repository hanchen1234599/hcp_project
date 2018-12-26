package com.hc.component.db.mysql.base;

import redis.clients.jedis.Jedis;

//import redis.clients.jedis.Jedis;

public class RedisCached {
	private String host = "localhost";
	private int port = 6379;
	Jedis jedis = null;
	
	public RedisCached(String host, int port) {
		this.host = host;
		this.port = port;
	}
	
	public void start() {
		this.jedis = new Jedis(this.host, this.port);
		//jedis.
	}
	
	public Jedis getRedis() {			
		return this.jedis;
	}
	
	public static void main(String args[]) {
//		Jedis jedis = new Jedis("localhost");
//        System.out.println("连接成功");
//        jedis.set("runoobkey", "www.runoob.com");
//        System.out.println("redis 存储的字符串为: "+ jedis.get("runoobkey"));
	}
}
