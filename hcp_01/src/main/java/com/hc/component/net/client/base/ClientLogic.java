package com.hc.component.net.client.base;

import com.hc.component.net.server.base.SessionImpl;
import com.hc.component.net.session.Session;
import com.hc.share.util.AtomicSessionID;
import com.hc.share.util.Trace;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelPromise;


public class ClientLogic extends ChannelInboundHandlerAdapter {
	ClientManagerImpl manager = null;
	Session session = null;
	public ClientLogic(ClientManagerImpl manager) {
		this.manager = manager;
	}
	protected Session getSession() {
		return this.session;
	}
	
	public void close(ChannelHandlerContext ctx, ChannelPromise promise) throws Exception {
		 Trace.logger.debug("连接失败");
		 ctx.close();
	}
	
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		Trace.logger.debug("client channelActive");
		long sessionID = AtomicSessionID.getOnlySessionID();
		Session session = new SessionImpl(ctx.channel(), sessionID);
		this.session = session;
		this.manager.connect(session);
	}
	@Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		Trace.logger.debug("client channelInactive");
		this.session = null;
		this.manager.unConnect();
    }
	
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		this.manager.recvData(session, (ByteBuf)msg);
	}
	
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		Trace.logger.debug("client exceptionCaught");
		this.manager.onConnectException();
	}
}
