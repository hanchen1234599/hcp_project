package com.hc.share.util;

import java.io.File;
import java.util.Iterator;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
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

	public static Element getElementByAttributeWithElementName(Element parent, String elementName, String key, String value) {
		@SuppressWarnings("unchecked")
		Iterator<Element> it = parent.elementIterator();
		while (it.hasNext()) {
			Element temp = it.next();
			if (temp.getName().equals(elementName)) {
				if (!temp.attribute(key).getText().equals(value))
					continue;
				return temp;
			}
		}
		return null;
	}

	public static Element getElementByAttribute(Element parent, String key, String value) {
		@SuppressWarnings("unchecked")
		Iterator<Element> it = parent.elementIterator();
		while (it.hasNext()) {
			Element temp = it.next();
			if (!temp.attribute(key).getText().equals(value))
				continue;
			return temp;
		}
		return null;
	}
}
