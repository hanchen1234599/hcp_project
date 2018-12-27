package point.login;

import org.dom4j.Document;
import org.dom4j.Element;

import com.hc.point.Point;
import com.hc.share.util.Trace;
import com.hc.share.util.XmlReader;

import point.login.logic.account.AccountManager;

public class LoginMain {
	
	public static void main(String[] args) {
		Trace.logger.info("login server is begin");
		try {
			Trace.logger.info(" registerModule");
			// 注册模块
			LoginApp.getInstace().registerModule(AccountManager.getInstance());
			LoginApp.getInstace().launchLogin();
			// 组件启动
			Document doc = XmlReader.getInstance().readFile("./config/login.xml");
			Element eRoot = doc.getRootElement();
			Element point =  eRoot.element("point");
			int nThread = Integer.parseInt(point.attribute("nthread").getText());
			LoginApp.getInstace().setAppNThead(nThread);
			Point p = new Point(point);
			p.parse();
			Thread.sleep(1200000000);
		} catch (Exception e) {
			Trace.fatal("start  error");
			e.printStackTrace();
		}
	}
}
