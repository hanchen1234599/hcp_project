package com.hc.component.db.mysql.base.desc;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public final class MysqlDB {
	public Map<String, MysqlTable> tables = new HashMap<String, MysqlTable>();

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for(Entry<String, MysqlTable> table : tables.entrySet()) {
			sb.append(table.getValue() + "\n");
		}
		return sb.toString();
	}
	
}
