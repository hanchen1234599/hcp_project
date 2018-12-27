package com.hc.component.net.session;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

public class Decode extends LengthFieldBasedFrameDecoder {
	
	public Decode(int maxFrameLength) {
		super(maxFrameLength, 0, 4);
	}
	
	 /**
	  * 取出完整包体
	  */
	protected Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception{
		if(ctx.channel().isActive() == false)
			return null;
		ByteBuf frame = (ByteBuf) super.decode(ctx, in);
		int len = frame.readInt();
		ByteBuf body = frame.slice(4, len);
		return body;
	}
}

