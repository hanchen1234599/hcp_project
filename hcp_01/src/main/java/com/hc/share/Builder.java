package com.hc.share;

import org.dom4j.Element;

@SuppressWarnings("rawtypes")
public interface Builder<T extends Listener>{
	void setListener(T listener);
	void build() throws Exception ;
	void build( Element element ) throws Exception;
}
