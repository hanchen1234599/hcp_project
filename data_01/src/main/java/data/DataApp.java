package data;

import org.dom4j.Element;
import com.hc.component.db.mysql.MysqlComponent;
import com.hc.component.net.client.ClientComponent;
import com.hc.component.net.server.ServerComponent;
import com.hc.share.util.XmlReader;
import share.server.config.MmoServerConfigTemp;

public class DataApp {
	private static DataApp instance = new DataApp();

	private DataApp() {
	}

	public static DataApp getInstace() {
		return instance;
	}

	public void start() {
	}

	public void init() throws Exception {
		MmoServerConfigTemp.getInstace().init("../share/config/servertypeconfig.xml");
		Element dataRoot = XmlReader.getInstance().readFile("../share/config/serverconfig.xml").getRootElement();
		Element dataPoint = XmlReader.getElementByAttributeWithElementName(dataRoot, "point", "type", "data");
		Element dataDbConfig = XmlReader.getElementByAttribute(dataRoot.element("dbconfig"), "id",
				dataPoint.element("roledb").attribute("id").getText());
		MysqlComponent mysql = MmoServerConfigTemp.getInstace().getMysql("data", "roledb",
				dataDbConfig.element("hikariconfig").getText());
		ServerComponent inner = MmoServerConfigTemp.getInstace().getServer("data", "inner",
				Integer.parseInt(dataPoint.element("inner").attribute("port").getText()));
		ClientComponent center = MmoServerConfigTemp.getInstace().getClient("data", "center",
				dataPoint.element("center").attribute("remoteip").getText(),
				Integer.parseInt(dataPoint.element("center").attribute("remoteport").getText()));
		mysql.build();
		inner.build();
		center.build();
	}

}
