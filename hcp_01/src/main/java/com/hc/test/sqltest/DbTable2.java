package com.hc.test.sqltest;

import com.hc.component.db.mysql.base.annotationtype.Constraints;
import com.hc.component.db.mysql.base.annotationtype.DBTable;
import com.hc.component.db.mysql.base.annotationtype.SQLInt;

@DBTable(name = "dbtable2")
public class DbTable2 {
	@SQLInt(constraints = @Constraints(primaryKey = true, allowNull = false, autoIncement = true))
	int t1;
	@SQLInt
	int t2;
	@SQLInt
	int t3;
	public int getT1() {
		return t1;
	}
	public void setT1(int t1) {
		this.t1 = t1;
	}
	public int getT2() {
		return t2;
	}
	public void setT2(int t2) {
		this.t2 = t2;
	}
	public int getT3() {
		return t3;
	}
	public void setT3(int t3) {
		this.t3 = t3;
	}
}
