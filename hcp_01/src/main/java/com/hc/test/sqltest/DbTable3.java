package com.hc.test.sqltest;

import com.hc.component.db.mysql.base.annotationtype.Constraints;
import com.hc.component.db.mysql.base.annotationtype.DBTable;
import com.hc.component.db.mysql.base.annotationtype.SQLBigInt;
import com.hc.component.db.mysql.base.annotationtype.SQLVarChar;

@DBTable(name = "dbtable3")
public class DbTable3 {
	@SQLBigInt(constraints = @Constraints(primaryKey = true, allowNull = false, autoIncement = true))
	long id;
	@SQLVarChar(length = 666)
	String name;
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getTest() {
		return test;
	}
	public void setTest(String test) {
		this.test = test;
	}
	String test;
}
