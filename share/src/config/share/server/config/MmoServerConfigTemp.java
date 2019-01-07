package share.server.config;

import java.util.HashMap;
import java.util.Iterator;
import org.dom4j.Document;
import org.dom4j.Element;
import com.hc.share.util.XmlReader;
import share.server.config.base.BaseConfig;
import share.server.config.base.ClientConfig;
import share.server.config.base.MysqlConfig;
import share.server.config.base.ServerConfig;

public class MmoServerConfigTemp {
	private static MmoServerConfigTemp instance = new MmoServerConfigTemp();

	private MmoServerConfigTemp() {
	}

	public static MmoServerConfigTemp getInstace() {
		return instance;
	}
	
	private String serviceName = "";
	private HashMap<String, MmoServerConfigType> mmo = new HashMap<>();
	
	//加载配置
	@SuppressWarnings({ "unchecked", "unlikely-arg-type" })
	public void init( String xmlPath ) throws Exception {
		Document doc = XmlReader.getInstance().readFile(xmlPath);
		Element root = doc.getRootElement();
		this.setServiceName(root.attribute("name").getText());
		Iterator<Element> rootIt = root.elementIterator();
		while(rootIt.hasNext()) {
			Element serverType = rootIt.next();
			int nThread = Integer.parseInt(serverType.attribute("nthread").getText());
			String type = serverType.attribute("type").getText();
			MmoServerConfigType configType = new MmoServerConfigType();
			configType.setnThread(nThread);
			this.mmo.put(type, configType);
			//configType.addBaseConfig(name, config);
			Iterator<Element> serverTypeIt = serverType.elementIterator();
			while(serverTypeIt.hasNext()) {
				Element component = serverTypeIt.next();
				BaseConfig config = null;
				if(component.attribute("type").equals("server")) {
					ServerConfig temp = new ServerConfig();
					temp.setBoosThreadNum(Integer.parseInt(component.attribute("boosthreadnum").getText()));
					temp.setInProtoLength(Integer.parseInt(component.attribute("inprotolength").getText()));
					temp.setListener(component.attribute("listener").getText());
					temp.setOutProtoLength(Integer.parseInt(component.attribute("outprotolength").getText()));
					temp.setWorkeThreadNum(Integer.parseInt(component.attribute("workethreadnum").getText()));
					config = temp;
				}else if(component.attribute("type").equals("client")) {
					ClientConfig temp = new ClientConfig();
					temp.setInProtoLength(Integer.parseInt(component.attribute("inprotolength").getText()));
					temp.setListener(component.attribute("listener").getText());
					temp.setOutProtoLength(Integer.parseInt(component.attribute("outprotolength").getText()));
					temp.setWorkeThreadNum(Integer.parseInt(component.attribute("workethreadnum").getText()));
					config = temp;
				}else if(component.attribute("type").equals("mysql")) {
					MysqlConfig temp = new MysqlConfig();
					temp.setWorkeThreadNum(Integer.parseInt(component.attribute("workethreadnum").getText()));
					temp.setListener(component.attribute("listener").getText());
					config = temp;
				}
				configType.addBaseConfig(component.attribute("name").getText(), config);
			}
		}
	}
	
	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}
	
	public ClientConfig getClientConfig(String type ,String name) {
		return this.mmo.get(type).getClientConfig(name);
	}
	public ServerConfig getServerConfig(String type ,String name) {
		return this.mmo.get(type).getServerConfig(name);
	}
	public MysqlConfig getMysqlConfig(String type ,String name) {
		return this.mmo.get(type).getMysqlConfig(name);
	}
}

class MmoServerConfigType {
	private int nThread = 1;
	private HashMap<String, BaseConfig> serverType = new HashMap<>();

	public ClientConfig getClientConfig(String name) {
		BaseConfig temp = this.serverType.get(name);
		if(!(temp instanceof ClientConfig)) {
			return null;
		}
		return (ClientConfig) temp;
	}
	public ServerConfig getServerConfig(String name) {
		BaseConfig temp = this.serverType.get(name);
		if(!(temp instanceof ServerConfig)) {
			return null;
		}
		return (ServerConfig) temp;
	}
	public MysqlConfig getMysqlConfig(String name) {
		BaseConfig temp = this.serverType.get(name);
		if(!(temp instanceof MysqlConfig)) {
			return null;
		}
		return (MysqlConfig) temp;
	}
	
	public int getnThread() {
		return nThread;
	}
	public void setnThread(int nThread) {
		this.nThread = nThread;
	}
	public void addBaseConfig(String name, BaseConfig config) {
		this.serverType.put(name, config);
	}
	public BaseConfig getBaseConfig(String name) {
		return this.serverType.get(name);
	}
}
