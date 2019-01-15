package share.proto.util;

import io.netty.buffer.ByteBuf;

public interface RecvContainerProtoByteBuf {
	void recv( int serviceContainerID, int serviceID, int sourceServiceContainerID , int sourceServiceID, int protocolID, ByteBuf buf );
}
