package share.service.service.base;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.hc.component.net.session.Session;

import io.netty.buffer.ByteBuf;
import share.proto.util.ProtoHelper;
import share.service.service.ServiceConnect;
@JsonIgnoreProperties(ignoreUnknown = true)
public class ServiceConnectImpl implements ServiceConnect {
	private int serviceID;
	private int serviceContainerID;
	private String remoteServiceType;
	@JsonIgnore
	private Session session;
	public Session getSession() {
		return session;
	}

	public void setSession(Session session) {
		this.session = session;
	}

	public int getServiceID() {
		return serviceID;
	}

	public int getServiceContainerID() {
		return serviceContainerID;
	}

	public String getRemoteServiceType() {
		return remoteServiceType;
	}
	
	public void setRemoteServiceType(String remoteServiceType) {
		this.remoteServiceType = remoteServiceType;
	}

	public void setServiceID(int serviceID) {
		this.serviceID = serviceID;
	}

	public void setServiceContainerID(int serviceContainerID) {
		this.serviceContainerID = serviceContainerID;
	}

	@Override
	public void send(int sourceServiceContainerID, int sourceServiceID, int protocolID, byte[] bytes) {
		ByteBuf body = ProtoHelper.createContainerProtoByteBuf(this.serviceContainerID, this.serviceID, sourceServiceContainerID, sourceServiceID, protocolID, bytes);
		this.session.send(body);
	}
}
