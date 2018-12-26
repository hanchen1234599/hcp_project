package com.hc.component.net.session;

import com.hc.share.Container;
import com.hc.share.exception.NetException;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
/**
 * @author hanchen
 *
 * @param <T> 接收数据类型
 * @param <Y> 发送数据类型
 */
public interface SessionContainer extends Container {
	void addSession(Session session) throws NetException;
	void removeSession(long sessionID);
	void removeSession(Session session);
	void removeSession(Channel channel);
	void recvData(Session session, ByteBuf body);
	Session getSession(Channel channel);
}
