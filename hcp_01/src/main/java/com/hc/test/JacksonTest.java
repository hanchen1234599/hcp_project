package com.hc.test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hc.share.util.Trace;

public class JacksonTest {

	public static void main(String[] args) throws IOException {
		TestBean bean = new TestBean();
		bean.setStringValue("hanchen#");
		bean.setDoubleValue(1.111d);
		bean.setFloatValue(1.2f);
		bean.setIntValue(1);
		bean.setLongValue(111111l);
		bean.setBooleanValue(true);
		bean.setMapValue(new HashMap<>());
		bean.getMapValue().put(1, "a");
		bean.getMapValue().put(2, "b");
		bean.setListValue(new ArrayList<>());
		bean.setSetValue(new HashSet<>());
		bean.getListValue().add(1l);
		bean.getListValue().add(1l);
		bean.getListValue().add(1l);
		bean.getListValue().add(1l);
		bean.getListValue().add(1l);
		bean.getSetValue().add(2l);
		bean.getSetValue().add(3l);
		bean.getSetValue().add(10l);
		bean.getSetValue().add(5l);
		bean.setBean(new TestParent());
		Trace.logger.info(bean);
		ObjectMapper json = new ObjectMapper();
		byte[] bytes = json.writeValueAsBytes(bean);
		String jsonStr = json.writeValueAsString(bean);
		Trace.logger.info(jsonStr);
		String test = "{\"aaa1\":\"aaa\",\"bean\":null,\"intValue\":1,\"doubleValue\":1.111,\"floatValue\":1.2,\"booleanValue\":true,\"longValue\":111111,\"mapValue\":{\"1\":\"a\",\"2\":\"b\"},\"listValue\":[1,1,1,1,1],\"setValue\":[2,3,5,10]}";
		//Trace.logger.info(test);
		Trace.logger.info("len:" + jsonStr.length() + " " +jsonStr);
		Trace.logger.info("len:" + bytes.length + " " + bytes);
		TestBean bean1 = json.readValue(test, TestBean.class);
		TestBean bean2 = json.readValue(bytes, TestBean.class);
		Trace.logger.info("bean1:" + bean1);
		Trace.logger.info("bean2:" + bean2);		
		Trace.logger.info("bean :" + bean);

		
		
		
//		bean1.setBean(bean);
//		byte[] bytes1 = json.writeValueAsBytes(bean1);
//		String jsonStr1 = json.writeValueAsString(bean1);
//		TestBean bean3 = json.readValue(jsonStr1, TestBean.class);
//		TestBean bean4 = json.readValue(bytes1, TestBean.class);
//		Trace.logger.info("bean3:" + bean3);
//		Trace.logger.info("bean4:" + bean4);
		
//		long start=System.currentTimeMillis();
//		for(int i=1;i<= 1000000; i++) {
//			jsonStr = json.writeValueAsString(bean);
//		}
//		long end=System.currentTimeMillis();
//		Trace.logger.info("writeValueAsString time:" + (end - start) );
//		start=System.currentTimeMillis();
//		for(int i=1;i< 1000000; i++) {
//			bytes = json.writeValueAsBytes(bean);
//		}
//		end=System.currentTimeMillis();
//		Trace.logger.info("writeValueAsBytes time:" + (end - start) );
//		start=System.currentTimeMillis();
//		for(int i=1;i< 1000000; i++) {
//			bean1 = json.readValue(jsonStr, TestBean.class);
//		}
//		end=System.currentTimeMillis();
//		Trace.logger.info("readValue str time:" + (end - start) );
//		start=System.currentTimeMillis();
//		for(int i=1;i< 1000000; i++) {
//			bean2 = json.readValue(bytes, TestBean.class);
//		}
//		end=System.currentTimeMillis();
//		Trace.logger.info("readValue bytes time:" + (end - start) );
	}
}

class TestParent{
	private String aaa = "aaa";

	public String getAaa() {
		return aaa;
	}

	public void setAaa(String aaa) {
		this.aaa = aaa;
	}

	@Override
	public String toString() {
		return "TestParent [aaa=" + aaa + "]";
	}
	
}
//@JsonIgnoreProperties(ignoreUnknown = true)
//class TestBean {
//	private TestParent bean;
//	private String stringValue;
//	private Integer intValue;
//	private Double doubleValue;
//	private Float floatValue;
//	private Boolean booleanValue;
//	private Long longValue;
//	private HashMap<Integer, String> mapValue = null;
//	private ArrayList<Long> listValue = null;
//	private HashSet<Long> setValue = null;
//	public TestParent getBean() {
//		return bean;
//	}
//
//	public void setBean(TestParent bean) {
//		this.bean = bean;
//	}
//
//	public ArrayList<Long> getListValue() {
//		return listValue;
//	}
//
//	public void setListValue(ArrayList<Long> listValue) {
//		this.listValue = listValue;
//	}
//
//	public HashSet<Long> getSetValue() {
//		return setValue;
//	}
//
//	public void setSetValue(HashSet<Long> setValue) {
//		this.setValue = setValue;
//	}
//	@Override
//	public String toString() {
//		return "TestBean{" + "stringValue='" + stringValue + '\'' + ", intValue=" + intValue + ", doubleValue="
//				+ doubleValue + ", floatValue='" + floatValue + '\'' + ", booleanValue='" + booleanValue + '\''
//				+ ", longValue='" + longValue + '\'' +
//				", listValue='" + listValue + '\'' +
//				", setValue='" + setValue + '\'' +
//				", bean='" + bean + '\'' +
//				", mapValue='" + mapValue + '\'' + '}';
//	}
//
//	public String getStringValue() {
//		return stringValue;
//	}
//
//	public void setStringValue(String stringValue) {
//		this.stringValue = stringValue;
//	}
//
//	public Integer getIntValue() {
//		return intValue;
//	}
//
//	public void setIntValue(Integer intValue) {
//		this.intValue = intValue;
//	}
//
//	public Double getDoubleValue() {
//		return doubleValue;
//	}
//
//	public void setDoubleValue(Double doubleValue) {
//		this.doubleValue = doubleValue;
//	}
//
//	public Float getFloatValue() {
//		return floatValue;
//	}
//
//	public void setFloatValue(Float floatValue) {
//		this.floatValue = floatValue;
//	}
//
//	public Boolean getBooleanValue() {
//		return booleanValue;
//	}
//
//	public void setBooleanValue(Boolean booleanValue) {
//		this.booleanValue = booleanValue;
//	}
//
//	public Long getLongValue() {
//		return longValue;
//	}
//
//	public void setLongValue(Long longValue) {
//		this.longValue = longValue;
//	}
//
//	public HashMap<Integer, String> getMapValue() {
//		return mapValue;
//	}
//
//	public void setMapValue(HashMap<Integer, String> mapValue) {
//		this.mapValue = mapValue;
//	}
//
//}
@JsonIgnoreProperties(ignoreUnknown = true)
class TestBean {
	private TestParent bean;
	private String stringValue;
	private Integer intValue;
	private Double doubleValue;
	private Float floatValue;
	private Boolean booleanValue;
	private Long longValue;
	private HashMap<Integer, String> mapValue = null;
	private ArrayList<Long> listValue = null;
	public TestParent getBean() {
		return bean;
	}
	public void setBean(TestParent bean) {
		this.bean = bean;
	}
	public String getStringValue() {
		return stringValue;
	}
	public void setStringValue(String stringValue) {
		this.stringValue = stringValue;
	}
	public Integer getIntValue() {
		return intValue;
	}
	public void setIntValue(Integer intValue) {
		this.intValue = intValue;
	}
	public Double getDoubleValue() {
		return doubleValue;
	}
	public void setDoubleValue(Double doubleValue) {
		this.doubleValue = doubleValue;
	}
	public Float getFloatValue() {
		return floatValue;
	}
	public void setFloatValue(Float floatValue) {    
		this.floatValue = floatValue;
	}
	public Boolean getBooleanValue() {
		return booleanValue;
	}
	public void setBooleanValue(Boolean booleanValue) {
		this.booleanValue = booleanValue;
	}
	public Long getLongValue() {
		return longValue;
	}
	public void setLongValue(Long longValue) {
		this.longValue = longValue;
	}
	public HashMap<Integer, String> getMapValue() {
		return mapValue;
	}
	public void setMapValue(HashMap<Integer, String> mapValue) {
		this.mapValue = mapValue;
	}
	public ArrayList<Long> getListValue() {
		return listValue;
	}
	public void setListValue(ArrayList<Long> listValue) {
		this.listValue = listValue;
	}
	public HashSet<Long> getSetValue() {
		return setValue;
	}
	public void setSetValue(HashSet<Long> setValue) {
		this.setValue = setValue;
	}
	private HashSet<Long> setValue = null;
	@Override
	public String toString() {
		return "TestBean{" + "stringValue='" + stringValue + '\'' + ", intValue=" + intValue + ", doubleValue="
				+ doubleValue + ", floatValue='" + floatValue + '\'' + ", booleanValue='" + booleanValue + '\''
				+ ", longValue='" + longValue + '\'' +
				", listValue='" + listValue + '\'' +
				", setValue='" + setValue + '\'' +
				", bean='" + bean + '\'' +
				", mapValue='" + mapValue + '\'' + '}';
	}
	}

