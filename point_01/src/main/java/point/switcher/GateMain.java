package point.switcher;

import java.util.ArrayList;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.Element;

import com.hc.point.Point;
import com.hc.share.util.Trace;
import com.hc.share.util.XmlReader;

public class GateMain {
	public static void main(String args[]) {
		Trace.logger.info("Gate is begin");
		List<Point> endPoints = new ArrayList<Point>();
		try {
			Trace.logger.info("pwd:" + System.getProperty("user.dir"));
			GateApp.getInstace().setCurServiceID(10);
			GateApp.getInstace().launchGate();
			Document doc = XmlReader.getInstance().readFile("./config/switcher.xml");
			Element eRoot = doc.getRootElement();
			@SuppressWarnings("unchecked")
			List<Element> points =  eRoot.elements("point");
			for(Element point: points) {
				Point p = new Point(point);
				p.parse();
				endPoints.add(p);
			}
			Thread.sleep(1000000000);
		} catch (Exception e) {
			Trace.fatal("gate start error");
			e.printStackTrace();
		}
	}
}
