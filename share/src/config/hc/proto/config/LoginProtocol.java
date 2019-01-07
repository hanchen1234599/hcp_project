package hc.proto.config;

import hc.proto.config.base.ProtocolLogic;

public class LoginProtocol {
	public static int LoginReq 			= 11;
	public static int LoginRsp 			= 14;
	public static int LoginPessReq 		= 12;
	public static int LoginPessRsp 		= 13;
	
	public static void init(ProtocolLogic protocolLogic) {
		protocolLogic.registerPtotoType(LoginReq, hc.login.PessCheck.LoginReq.newBuilder());
		protocolLogic.registerPtotoType(LoginRsp, hc.login.PessCheck.LoginRsp.newBuilder());
		protocolLogic.registerPtotoType(LoginPessReq, hc.login.PessCheck.LoginPessReq.newBuilder());
		protocolLogic.registerPtotoType(LoginPessRsp, hc.login.PessCheck.LoginPessRsp.newBuilder());
	}
}
