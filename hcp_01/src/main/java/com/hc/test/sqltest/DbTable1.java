package com.hc.test.sqltest;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import com.hc.component.db.mysql.base.MysqlBean;
import com.hc.component.db.mysql.base.annotationtype.Constraints;
import com.hc.component.db.mysql.base.annotationtype.DBTable;
import com.hc.component.db.mysql.base.annotationtype.SQLBigInt;
import com.hc.component.db.mysql.base.annotationtype.SQLDouble;
import com.hc.component.db.mysql.base.annotationtype.SQLInt;
import com.hc.component.db.mysql.base.annotationtype.SQLVarChar;

@SuppressWarnings("serial")
@DBTable(name = "dbtable1")
public class DbTable1 extends MysqlBean {
	/**
	 * 
	 */
	@SQLInt( constraints = @Constraints(allowNull = false, primaryKey = true, autoIncement = true))
	int a = 1;
	@SQLBigInt
	long b = 2;
	@SQLVarChar(length = 100)
	String c = "c";
	@SQLDouble
	double d = 1.1;
	public int getA() {
		return a;
	}
	public void setA(int a) {
		this.a = a;
	}
	public long getB() {
		return b;
	}
	public void setB(long b) {
		this.b = b;
	}
	public String getC() {
		return c;
	}
	public void setC(String c) {
		this.c = c;
	}
	public double getD() {
		return d;
	}
	public void setD(double d) {
		this.d = d;
	}
	
	public static void main(String args[]) throws Exception {
		DbTable1 d = new DbTable1();
		d.setA(1);
		d.setB(2);
		d.setC("3");
		ByteArrayOutputStream buff = new ByteArrayOutputStream();
		ObjectOutputStream stream = new ObjectOutputStream(buff);
		d.setA(5);
		stream.writeObject(d);
		d.setA(4);
		buff.reset();
		stream.close();
		stream = new ObjectOutputStream(buff);
		stream.writeObject(d);
		byte[] bs = buff.toByteArray();
		System.out.println("array: " + bs);
		
		
		stream.writeObject(d);
		ObjectInputStream inStream = new ObjectInputStream(new ByteArrayInputStream(bs));
		DbTable1 d1 = (DbTable1) inStream.readObject();
		System.out.println("A: " + d1.getA());
		System.out.println("B: " + d1.getB());
		System.out.println("C: " + d1.getC());
	}
}
