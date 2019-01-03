package hc.protoconfig;

import java.util.HashMap;
import java.util.concurrent.ExecutorService;

import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.MessageLite;
import com.hc.share.util.Trace;
import io.netty.buffer.ByteBuf;
/**
 * 这个类为非线程安全的  使用的时候一定要注意 不能以单例来实现
 * @author hanchen
 */
public class ProtocolLogic {
	private HashMap<Integer, MessageLite.Builder> protoTypes = new HashMap<>();
	private ExecutorService exec = null; // 这里支持单例线程池
	public void init() {
		GateProtocol.init(this);
		HeadProtocol.init(this);
		LoginProtocol.init(this);
		CenterProtocol.init(this);
	}
	
	public void exec( Runnable run ) {
		this.exec.execute(run);
	}
	
	public void setExec(ExecutorService exec) {
		this.exec = exec;
	}
	
	public MessageLite.Builder getBuilder(int protoID){
		MessageLite.Builder builder = protoTypes.get(protoID);
		if(builder != null)
			builder.clear();
		return builder;
	}
	
	public void registerPtotoType(int pid, MessageLite.Builder builder) {
		if( this.protoTypes.get(pid) != null ) {
			Trace.logger.info("协议重复注册");
			Runtime.getRuntime().exit(1);
		}
		this.protoTypes.put(pid, builder);
	}
	
	/**
	 * 非线程安全
	 * @param protoID
	 * @param buf
	 * @param callback
	 */
	public void recvMessage2Protocol(int protoID, ByteBuf buf, RecvMessage2Protobuf callback) {
		if(this.exec == null)
			return;
		this.exec.execute(()->{
			MessageLite.Builder builder = this.protoTypes.get(protoID);
			if(builder == null) {
				Trace.logger.info("协议protoID:" + protoID + "未定义");
				return;
			}
			builder.clear();
			int len = buf.readableBytes();
			byte[] buff = new byte[len];
			buf.getBytes(0, buff);
			try {
				callback.recvMessage2Protobuf(builder.mergeFrom(buff).build());
			} catch (InvalidProtocolBufferException e) {
				Trace.logger.info("协议protoID:" + protoID + "解析错误");
				e.printStackTrace();
			}
		});
	}
}
