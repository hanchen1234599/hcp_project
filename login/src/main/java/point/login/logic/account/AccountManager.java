package point.login.logic.account;

import java.sql.SQLException;

import com.google.protobuf.InvalidProtocolBufferException;
import com.hc.component.net.session.Session;
import com.hc.share.util.ProtoHelper;
import com.hc.share.util.Trace;

import io.netty.buffer.ByteBuf;
import point.login.LoginApp;
import point.login.logic.LogicAbstract;

public class AccountManager extends LogicAbstract {
	private static AccountManager instance = new AccountManager();
	private String moduleName = "account";
	private volatile boolean isOpen = false;
	private AccountManager() {}
	public static AccountManager getInstance() {
		return instance;
	} 
	@Override
	public void onLaunchLogin() {
		Trace.logger.info("onLauchLogin");
		// 预留 11~20 协议为账号协议
		LoginApp.getInstace().registerProtoBufProtoProtocol(12, this);
	}
	@Override
	public void onDbComplate() {
		isOpen = true;
		Trace.logger.info("on db complete");
	}
	@Override
	public String getModuleName() {
		return this.moduleName;
	}
	@Override
	public void onGateProto(Session session, int pid, ByteBuf body) {
		Trace.logger.info("recv protobuf pid :" + pid);
		if(isOpen == false)
			return;
		if(pid == hc.protoconfig.LoginProtocol.LoginPessReq) {
			int bodyLen = body.readableBytes();
			byte[] bodyBuff = new byte[bodyLen];
			body.getBytes(0, bodyBuff, 0, bodyLen);
			try {
				hc.login.PessCheck.LoginPessReq loginReq = hc.login.PessCheck.LoginPessReq.newBuilder().mergeFrom(bodyBuff).build();
				String accountName = loginReq.getAccountName();
				long sessionID = loginReq.getSessionID();
				Trace.logger.info("账号 " + accountName + " query. " + " time:" + System.currentTimeMillis());
				LoginApp.getInstace().getDb().findASync((rs)->{
					hc.login.PessCheck.LoginPessRsp.Builder loginPessRspBuilder = hc.login.PessCheck.LoginPessRsp.newBuilder();
					try {
						if(rs.next() == false)
							return;
						long userID = rs.getLong(1);
						Trace.logger.info("账号: " + accountName + " userID: " + userID  + " time:" + System.currentTimeMillis());
						loginPessRspBuilder.setResult(0);
						loginPessRspBuilder.setSessionID(sessionID);
						loginPessRspBuilder.setUserID(userID);
						session.send(ProtoHelper.createProtoBufByteBuf(0, 0, hc.protoconfig.LoginProtocol.LoginPessRsp, loginPessRspBuilder.build().toByteArray()));
					} catch (SQLException e) {
						loginPessRspBuilder.setResult(1); // 数据库操作异常
						loginPessRspBuilder.setSessionID(sessionID);
						loginPessRspBuilder.setUserID(0);
						session.send(ProtoHelper.createProtoBufByteBuf(0, 0, hc.protoconfig.LoginProtocol.LoginPessRsp, loginPessRspBuilder.build().toByteArray()));
						Trace.logger.info(e);
					}
				}, LoginApp.getInstace().getAppExec(), "select login( ?, 'evo', '' )", accountName);
			} catch (InvalidProtocolBufferException e) {
				e.printStackTrace();
			}
		}
	}
}
