package com.hc.test;

import com.hc.share.util.Trace;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

public class Main4 {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		ByteBuf buf = Unpooled.copyInt(1024);
		ByteBuf buf1 = Unpooled.copyInt(6096);
		ByteBuf buf2 = Unpooled.wrappedBuffer(buf, buf1);
		int readIndex = buf2.readerIndex();
		int writeIndex = buf2.writerIndex();
		
		
		
		
		Trace.info("-------------------------");
		Trace.info("readIndex:" + readIndex);
		Trace.info("writeIndex:" + writeIndex);
		Trace.info("-------------------------");
		byte[] bytes = new byte[8];
//		Trace.info(buf2.readInt());
//		Trace.info(buf2.readInt());
//		Trace.info(buf2.readInt());
//		Trace.info(buf2.readInt());
		//Trace.info(buf2.readInt());
		//buf2.readInt();
		//ByteBuf buf3 = buf2.slice(0, 4);
		//ByteBuf buf4 = buf2.slice(4, 4);
//		//buf2.getBytes(4, bytes);
		//Trace.info("3 :" + buf3.readInt());
		//Trace.info("4 :" + buf4.readInt());
//		Trace.info( "end" );
		Trace.info(" " + buf2.readableBytes());
		buf2.readInt();
		buf2.getBytes(0, bytes);
		Trace.info(Unpooled.wrappedBuffer(bytes).slice(4, 4).readInt());
		
	}

}
