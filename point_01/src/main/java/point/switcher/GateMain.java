package point.switcher;

import org.dom4j.Document;
import org.dom4j.Element;

import com.hc.point.Point;
import com.hc.share.util.Trace;
import com.hc.share.util.XmlReader;

public class GateMain {
	public static void main(String args[]) {
		Trace.logger.info("Gate is begin");
		try {
			Trace.logger.info("pwd:" + System.getProperty("user.dir"));
			GateApp.getInstace().setCurServiceID(10);
			GateApp.getInstace().launchGate();
			Document doc = XmlReader.getInstance().readFile("./config/switcher.xml");
			Element eRoot = doc.getRootElement();
			Element point =  eRoot.element("point");
			int nThread = Integer.parseInt(point.attribute("nthread").getText());
			GateApp.getInstace().setAppNThead(nThread);
			Point p = new Point(point);
			p.parse();
			Thread.sleep(1000000000);
		} catch (Exception e) {
			Trace.fatal("gate start error");
			e.printStackTrace();
		}
	}
}
