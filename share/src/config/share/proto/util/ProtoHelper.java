package share.proto.util;

import com.google.protobuf.InvalidProtocolBufferException;
import com.hc.share.util.Trace;

import hc.head.ProtoHead.Head.ProtoType;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

public class ProtoHelper {
	public static ByteBuf createProtoBufByteBuf(int srcID, int desID, int pid, byte[] protoBytes ) {
		hc.head.ProtoHead.Head.Builder rspHead = hc.head.ProtoHead.Head.newBuilder();
		rspHead.setSrcID(srcID);
		rspHead.setDesID(desID);
		rspHead.setType(ProtoType.PROTOBUF);
		rspHead.setProtoID(pid);
		
		byte[] headBuff = rspHead.build().toByteArray();
		short rspHeadLen = (short) headBuff.length;
		ByteBuf rspHeadLenBuf = Unpooled.copyShort(rspHeadLen);
		ByteBuf rspHeadBuf = Unpooled.wrappedBuffer(headBuff);
		ByteBuf rspBodyBuf = Unpooled.wrappedBuffer(protoBytes);
		return Unpooled.wrappedBuffer( rspHeadLenBuf, rspHeadBuf, rspBodyBuf );
	}
	// 这个函数是线程安全的  适合底层用  
	public static void recvProtoBufByteBuf(ByteBuf buf, RecvProtoBufByteBuf callback) {
		int bufLenght = buf.readableBytes();
		short headLen = buf.readShort();
		ByteBuf bufHead = buf.slice(2, headLen);
		byte[] bytes = new byte[headLen];
		bufHead.getBytes(0, bytes);
		try {
			hc.head.ProtoHead.Head.Builder header = hc.head.ProtoHead.Head.newBuilder().mergeFrom(bytes);
			hc.head.ProtoHead.Head head = header.build();
			int srcID = head.getSrcID();
			int desID = head.getDesID();
			ProtoType protoType = head.getType();
			int protoID = head.getProtoID();
			ByteBuf bufBody = buf.slice(2 + headLen, bufLenght - headLen- 2);
			callback.recvProtoBufByteBuf(true, srcID, desID, protoType, protoID, bufBody);
		} catch (InvalidProtocolBufferException e) {
			Trace.logger.info(e);
			callback.recvProtoBufByteBuf(false, 0, 0, ProtoType.PROTOBUF, 0, null);
		}
	}
}

