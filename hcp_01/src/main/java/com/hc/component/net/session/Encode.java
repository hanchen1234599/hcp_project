package com.hc.component.net.session;

import com.hc.share.util.Trace;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
/**
 * encode Э�����
 * 包长度  + 头长度 + 头body + body
 * @author hanchen
 */

public class Encode extends MessageToByteEncoder<ByteBuf> {
	private int maxFrameLength = 1024 * 64;
	public Encode(int maxFrameLength) {
		this.maxFrameLength = maxFrameLength;
	}
	@Override
	protected void encode(ChannelHandlerContext ctx, ByteBuf msg, ByteBuf out) throws Exception {
		if(msg == null)
			return ;
		int len = msg.readableBytes();
		if(len > maxFrameLength) {
			Trace.debug( "写入协议包太大" );
			return;
		}
		ByteBuf body = Unpooled.wrappedBuffer(Unpooled.copyInt(len), msg);
		out.writeBytes(body);
	}
}