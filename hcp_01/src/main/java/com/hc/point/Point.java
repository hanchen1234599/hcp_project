package com.hc.point;

import java.util.List;
import org.dom4j.Document;
import org.dom4j.Element;
import com.hc.share.Component;
import com.hc.share.util.Trace;
import com.hc.share.util.XmlReader;

public class Point {
	private Element point = null;
	private String name = "";
	private String type = "";

	@SuppressWarnings("unchecked")
	public Point(String pointXml) throws Exception {
		Document doc = XmlReader.getInstance().readFile(pointXml);
		Element root = doc.getRootElement();
		if (!root.getName().equals("project"))
			throw new Exception("point xml file struct error");
		List<Element> list = root.elements("point");
		if (list.size() != 1)
			throw new Exception("point xml file struct error");
		this.point = list.get(0);
		this.name = this.point.attributeValue("name");
		this.type = this.point.attributeValue("type");
	}

	public Point(Element point) throws Exception {
		this.point = point;
		this.name = this.point.attributeValue("name");
		this.type = this.point.attributeValue("type");
	}

	@SuppressWarnings("unchecked")
	public void parse() {
		List<Element> components = this.point.elements("component");
		try {
			for (int i = 0; i < components.size(); i++) {
				Element componect = components.get(i);
				Component.createComponent(componect.attributeValue("type")).build( componect );
			}
		} catch (Exception e) {
			Trace.logger.info("Point xml config error");
			Trace.logger.fatal(e);
			Runtime.getRuntime().exit(0);
		}
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
}
