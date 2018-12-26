package com.hc.test;

import com.hc.share.util.Trace;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

public class Main2 {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		ByteBuf buf = Unpooled.copyShort(100);
		//buf.writeInt(200);
		Trace.info(buf.readShort());
//		ByteBuf buf1 = buf.slice(0, 4);
//		Trace.info(buf1.readInt());
//		Trace.info(buf.readInt());
	}

}
