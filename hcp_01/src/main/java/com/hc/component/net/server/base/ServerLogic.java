package com.hc.component.net.server.base;

import com.hc.component.net.session.Session;
import com.hc.component.net.session.SessionContainer;
import com.hc.share.util.AtomicSessionID;
import com.hc.share.util.Trace;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class ServerLogic extends ChannelInboundHandlerAdapter  {
	SessionContainer container = null;
	public ServerLogic(SessionContainer container) {
		this.container = container;
	}
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		long sessionID = AtomicSessionID.getOnlySessionID();
		Session session = new SessionImpl(ctx.channel(), sessionID); 
		this.container.addSession(session);
	}
	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		this.container.removeSession(ctx.channel());
	}
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		if(msg == null) {
			Trace.debug("连接已经断开 msg = null");
		}
		Session session = this.container.getSession(ctx.channel());
		this.container.recvData(session, (ByteBuf)msg);
	}
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		Trace.logger.info(cause);
		Session session = this.container.getSession(ctx.channel());
		this.container.OnExceptionCaught(session, cause);
	}
}
