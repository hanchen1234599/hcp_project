package share.service.service;

import com.hc.share.Listener;
import io.netty.buffer.ByteBuf;

/**
 * 服务接口
 * 
 * @author hanchen
 */
public interface Service extends Listener<ServiceManager> {
	void onServiceMessage(ServiceConnect connect, ByteBuf body);

	void onNoticeServiceConnect(ServiceConnect conn);

	void onNoticeServiceUnConnect(ServiceConnect conn);
}
