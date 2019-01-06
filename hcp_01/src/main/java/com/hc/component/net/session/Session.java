package com.hc.component.net.session;

//import hc.server.passcheck.PassCheck;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
/**
 * 
 * @author hanchen
 * @param <T> 发送数据类型
 */
public interface Session {
	long getSessionID();
	void send(ByteBuf body);
	Channel getChannel();
	void setParamete( Object arg );
	Object getParamete();
}
 