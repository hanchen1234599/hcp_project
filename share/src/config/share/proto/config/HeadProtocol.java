package share.proto.config;

import share.proto.config.base.ProtocolLogic;

public class HeadProtocol {
	public static int Head = 0;
	public static void init(ProtocolLogic protocolLogic) {
		protocolLogic.registerPtotoType(Head, hc.head.ProtoHead.Head.newBuilder());
	}
}
