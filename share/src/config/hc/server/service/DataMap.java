package hc.server.service;

import java.util.HashMap;

public class DataMap {
	private HashMap<Integer, Data> datas = new HashMap<>();

	public HashMap<Integer, Data> getDatas() {
		return datas;
	}

	public void setDatas(HashMap<Integer, Data> datas) {
		this.datas = datas;
	}
}
