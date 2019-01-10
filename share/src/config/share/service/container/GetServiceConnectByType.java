package share.service.container;

import java.util.List;

import share.service.service.ServiceConnect;

public interface GetServiceConnectByType {
	void callback( List<ServiceConnect> list );
}
