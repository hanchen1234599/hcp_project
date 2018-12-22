package point.login;

//import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.Element;

import com.hc.point.Point;
import com.hc.share.util.Trace;
import com.hc.share.util.XmlReader;

import point.login.base.LoginApp;

public class Login {
	
	public static void main(String[] args) {
		Trace.logger.info("login server is begin");
		List<Point> endPoints = new ArrayList<Point>();
		try {
			Trace.logger.info("pwd:" + System.getProperty("user.dir"));
			Document doc = XmlReader.getInstance().readFile("./config/login.xml");
			Element eRoot = doc.getRootElement();
			@SuppressWarnings("unchecked")
			List<Element> points =  eRoot.elements("point");
			for(Element point: points) {
				Point p = new Point(point);
				p.parse();
				endPoints.add(p);
			}
			
			LoginApp.getInstace().launchLogin();
			Thread.sleep(10000000000l);
		} catch (Exception e) {
			Trace.fatal("start error");
			e.printStackTrace();
		}
	}
}
