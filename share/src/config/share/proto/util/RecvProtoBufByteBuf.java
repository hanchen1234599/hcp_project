package share.proto.util;

import hc.head.ProtoHead.Head.ProtoType;
import io.netty.buffer.ByteBuf;

public interface RecvProtoBufByteBuf {
	void recvProtoBufByteBuf( boolean result, int srcID, int desID, ProtoType protoType, int protoID, ByteBuf buf );
}
