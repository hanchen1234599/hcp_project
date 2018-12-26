package com.hc.test;

import java.util.List;

import org.dom4j.Document;
import org.dom4j.Element;

import com.hc.share.util.Trace;
import com.hc.share.util.XmlReader;

public class MainLog {
	public static void main(String[] args) throws Exception {
		Document doc = XmlReader.getInstance().readFile("C:/Users/hanchen/Desktop/aaa.xml");
		Element root = doc.getRootElement();
		if(!root.getName().equals("project"))
			throw new Exception("point xml file struct error");
		@SuppressWarnings("unchecked")
		List<Element> list = root.elements("point");
		Trace.info("size" + list.size());
		if(list.size() != 1)
			throw new Exception("point xml file struct error");
	}
}
