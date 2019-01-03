package hc.protoconfig;

public class CenterProtocol {
	public static int CenterCreateServerConnectReq 			= 121;
	public static int CenterCreateServerConnectRsp 			= 122;
	public static int CenterServerCheckReq 					= 123;
	public static int CenterServerCheckRsp 					= 124;
	public static int CenterServerMessageAskReq 			= 125;
	public static int CenterServerMessageAskRsp 			= 126;
	
	public static void init(ProtocolLogic protocolLogic) {
		protocolLogic.registerPtotoType(CenterCreateServerConnectReq, hc.center.CenterProto.CenterCreateServerConnectReq.newBuilder());
		protocolLogic.registerPtotoType(CenterCreateServerConnectRsp, hc.center.CenterProto.CenterCreateServerConnectRsp.newBuilder());
		protocolLogic.registerPtotoType(CenterServerCheckReq, hc.center.CenterProto.CenterServerCheckReq.newBuilder());
		protocolLogic.registerPtotoType(CenterServerCheckRsp, hc.center.CenterProto.CenterServerCheckRsp.newBuilder());
		protocolLogic.registerPtotoType(CenterServerMessageAskReq, hc.center.CenterProto.CenterServerMessageAskReq.newBuilder());
		protocolLogic.registerPtotoType(CenterServerMessageAskRsp, hc.center.CenterProto.CenterServerMessageAskRsp.newBuilder());
	}
}
