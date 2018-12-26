package com.hc.test;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.List;

import com.hc.component.db.mysql.base.annotationtype.Constraints;
import com.hc.component.db.mysql.base.annotationtype.DBTable;
import com.hc.component.db.mysql.base.annotationtype.SQLBigInt;
import com.hc.component.db.mysql.base.annotationtype.SQLDouble;
import com.hc.component.db.mysql.base.annotationtype.SQLInt;
import com.hc.component.db.mysql.base.annotationtype.SQLMediumBlob;
import com.hc.component.db.mysql.base.annotationtype.SQLVarChar;
import com.hc.component.db.mysql.base.desc.MysqlConstraints;
import com.hc.component.db.mysql.base.desc.MysqlDB;
import com.hc.component.db.mysql.base.desc.MysqlFiled;
import com.hc.component.db.mysql.base.desc.MysqlTable;
import com.hc.component.db.mysql.base.desc.MysqlType;
import com.hc.share.util.PackageUtil;
import com.hc.share.util.Trace;

public class TestMysql1 {

	public static void main(String[] args) throws ClassNotFoundException {
		String packet = "com.hc.test";
		List<String> list = PackageUtil.getClassName(packet);
		MysqlDB db = new MysqlDB();
		if (list != null) {
			for (String className : list) {
				MysqlTable mysqlTable = getTableStruct(className);
				if(mysqlTable != null)
					db.tables.put(mysqlTable.tableName, mysqlTable);
			}
		}
		Trace.info("\n" + db);
	}

	private static MysqlTable getTableStruct(String className) throws ClassNotFoundException {
		Class<?> cl = Class.forName(className);
		DBTable dbtable = cl.getAnnotation(DBTable.class);
		if (dbtable != null) {
			String tableName = dbtable.name();
			if (tableName.equals("") == false) {
				MysqlTable mysqlTable = new MysqlTable();
				mysqlTable.tableName = tableName.toLowerCase();
				for (Field field : cl.getDeclaredFields()) {
					Annotation[] annotations = field.getAnnotations();
					if (annotations.length != 1) {
						continue;
					}
					String columnName = field.getName().toLowerCase();
					MysqlFiled mysqlFileld = new MysqlFiled();
					if (annotations[0] instanceof SQLBigInt) {
						SQLBigInt sqlType = (SQLBigInt) annotations[0];
						mysqlFileld.type = MysqlType.BIGINT;
						mysqlFileld.length = sqlType.length();
						mysqlFileld.constraints = getConstraints(sqlType.constraints());
						mysqlTable.fields.put(columnName, mysqlFileld);
					}else if (annotations[0] instanceof SQLDouble) {
						SQLDouble sqlType = (SQLDouble) annotations[0];
						mysqlFileld.type = MysqlType.DOUBLT;
						mysqlFileld.constraints = getConstraints(sqlType.constraints());
						mysqlTable.fields.put(columnName, mysqlFileld);
					}else if (annotations[0] instanceof SQLInt) {
						SQLInt sqlType = (SQLInt) annotations[0];
						mysqlFileld.type = MysqlType.INT;
						mysqlFileld.length = sqlType.length();
						mysqlFileld.constraints = getConstraints(sqlType.constraints());
						mysqlTable.fields.put(columnName, mysqlFileld);
					}else if (annotations[0] instanceof SQLVarChar) {
						SQLVarChar sqlType = (SQLVarChar) annotations[0];
						mysqlFileld.type = MysqlType.VARCHAR;
						mysqlFileld.length = sqlType.length();
						mysqlFileld.constraints = getConstraints(sqlType.constraints());
						mysqlTable.fields.put(columnName, mysqlFileld);
					}else if (annotations[0] instanceof SQLMediumBlob) {
						SQLMediumBlob sqlType = (SQLMediumBlob) annotations[0];
						mysqlFileld.type = MysqlType.MEDIUMBLOB;
						mysqlFileld.constraints = getConstraints(sqlType.constraints());
						mysqlTable.fields.put(columnName, mysqlFileld);
					}else {
						Trace.warn("Mysql javaBean 有未知注解");
					}
				}
				return mysqlTable;
			}	
		}
		return null;
	}

	private static MysqlConstraints getConstraints(Constraints constraints) { // 获取字段约束属性
		MysqlConstraints myconsraints = new MysqlConstraints();
		myconsraints.allowNull = constraints.allowNull();
		myconsraints.primaryKey = constraints.primaryKey();
		//consraints.unique = constraints.unique();
		myconsraints.autoIncement = constraints.autoIncement();
		return myconsraints;
	}
}
