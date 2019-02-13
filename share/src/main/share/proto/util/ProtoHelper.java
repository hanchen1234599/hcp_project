package share.proto.util;

import com.google.protobuf.InvalidProtocolBufferException;
import com.hc.share.util.Trace;

import hc.head.ProtoHead.Head.ProtoType;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
/**
 * @author hanchen
 */
public class ProtoHelper {
	public static ByteBuf createContainerProtoByteBuf(int serviceContainerID, int serviceID,
			int sourceServiceContainerID, int sourceServiceID, int protocolID, byte[] protoBytes) {
		hc.share.ProtoShare.ContainerHead.Builder builder = hc.share.ProtoShare.ContainerHead.newBuilder();
		builder.setServiceContainerID(serviceContainerID);
		builder.setServiceID(serviceID);
		builder.setSourceServiceContainerID(sourceServiceContainerID);
		builder.setSourceServiceID(sourceServiceID);
		builder.setProtocolID(protocolID);
		byte[] headBuff = builder.build().toByteArray();
		short rspHeadLen = (short) headBuff.length;
		ByteBuf rspHeadLenBuf = Unpooled.copyShort(rspHeadLen);
		ByteBuf rspHeadBuf = Unpooled.wrappedBuffer(headBuff);
		ByteBuf rspBodyBuf = Unpooled.wrappedBuffer(protoBytes);
		return Unpooled.wrappedBuffer(rspHeadLenBuf, rspHeadBuf, rspBodyBuf);
	}

	public static void recvContainerProtoByteBuf(ByteBuf buf, RecvContainerProtoByteBuf callback) {
		int bufLenght = buf.readableBytes();
		short headLen = buf.readShort();
		ByteBuf bufHead = buf.slice(2, headLen);
		byte[] bytes = new byte[headLen];
		bufHead.getBytes(0, bytes);
		try {
			hc.share.ProtoShare.ContainerHead.Builder header = hc.share.ProtoShare.ContainerHead.newBuilder()
					.mergeFrom(bytes);
			hc.share.ProtoShare.ContainerHead head = header.build();
			int serviceContainerID = head.getServiceContainerID();
			int serviceID = head.getServiceID();
			int sourceServiceContainerID = head.getSourceServiceContainerID();
			int sourceServiceID = head.getSourceServiceID();
			int protocolID = head.getProtocolID();
			ByteBuf bufBody = buf.slice(2 + headLen, bufLenght - headLen - 2);
			callback.recv(serviceContainerID, serviceID, sourceServiceContainerID, sourceServiceID, protocolID,
					bufBody);
		} catch (InvalidProtocolBufferException e) {
			Trace.logger.info(e);
			callback.recv(0, 0, 0, 0, 0, buf);
		}
	}

	public static ByteBuf createProtoBufByteBuf(int srcID, int desID, int pid, byte[] protoBytes) {
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
		return Unpooled.wrappedBuffer(rspHeadLenBuf, rspHeadBuf, rspBodyBuf);
	}

	/**
	 * 这个函数是线程安全的 适合底层用
	 * @param buf
	 * @param callback
	 */
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
			ByteBuf bufBody = buf.slice(2 + headLen, bufLenght - headLen - 2);
			callback.recvProtoBufByteBuf(true, srcID, desID, protoType, protoID, bufBody);
		} catch (InvalidProtocolBufferException e) {
			Trace.logger.info(e);
			callback.recvProtoBufByteBuf(false, 0, 0, ProtoType.PROTOBUF, 0, null);
		}
	}
}
