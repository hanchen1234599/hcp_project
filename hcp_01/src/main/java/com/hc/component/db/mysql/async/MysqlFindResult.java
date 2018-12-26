package com.hc.component.db.mysql.async;

import java.sql.ResultSet;

public interface MysqlFindResult {
	void dealResult(ResultSet result);
}
