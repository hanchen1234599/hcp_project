package com.hc.share.util;

import java.io.File;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.io.SAXReader;

public class XmlReader {
	private static XmlReader instance = new XmlReader();
	private SAXReader saxReader = new SAXReader();
	
	public static XmlReader getInstance() {
		return instance;
	}
	public synchronized Document readStr(String xml) throws DocumentException {
		return this.saxReader.read(xml);
	}
	public synchronized Document readFile(String fileName) throws DocumentException {
		return this.saxReader.read(new File(fileName));
	}
}
