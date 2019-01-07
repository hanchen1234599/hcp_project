package share.proto.config;

import share.proto.config.base.ProtocolLogic;

public class GateProtocol {
	public static int S2GReq 			= 21;
	public static int S2GRsp 			= 22;
	public static int Ping 				= 23;
	public static int Pong 				= 24;
	
	public static void init(ProtocolLogic protocolLogic) {
		protocolLogic.registerPtotoType(S2GReq, hc.gate.S2GConnect.S2GReq.newBuilder());
		protocolLogic.registerPtotoType(S2GRsp, hc.gate.S2GConnect.S2GRsp.newBuilder());
		protocolLogic.registerPtotoType(Ping, hc.gate.S2GConnect.Ping.newBuilder());
		protocolLogic.registerPtotoType(Pong, hc.gate.S2GConnect.Pong.newBuilder());
	}
}
