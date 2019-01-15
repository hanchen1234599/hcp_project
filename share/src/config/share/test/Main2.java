package share.test;

import java.util.HashMap;

import com.fasterxml.jackson.databind.ObjectMapper;
import share.service.service.base.ServiceConnectImpl;

public class Main2 {

	public static void main(String[] args) throws Exception {
		ServiceConnectImpl impl = new ServiceConnectImpl();
		impl.setRemoteServiceType("data");
		impl.setServiceContainerID(1);
		impl.setServiceID(1);
		ObjectMapper json = new ObjectMapper();
		String jsonStr = json.writeValueAsString(impl);
		System.out.println(jsonStr);
		ServiceConnectImpl impl1 = json.readValue(jsonStr, ServiceConnectImpl.class);
		System.out.println(impl1.getServiceContainerID());
		System.out.println(impl1.getRemoteServiceType());
		System.out.println(impl1.getServiceID());
		
		
		HashMap<Integer, Boolean> testMap = new HashMap<>();
		testMap.put(1, true);
		System.out.println(testMap.get(1) != null && testMap.get(1) == true);
	}

}
