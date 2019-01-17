package share.test.mysql;
@Deprecated
public class MysqlUtil {
//	public static String getDbDesc(String packetPath) throws ClassNotFoundException {
//		return getMysqlDb(packetPath).toString();
//	}
//
//	public static MysqlDB getMysqlDb(String packetPath) throws ClassNotFoundException {
//		List<String> list = PackageUtil.getClassName(packetPath);
//		MysqlDB db = new MysqlDB();
//		if (list != null) {
//			for (String className : list) {
//				MysqlTable mysqlTable = getTableStruct(className);
//				if (mysqlTable != null)
//					db.tables.put(mysqlTable.tableName, mysqlTable);
//			}
//		}
//		return db;
//	}
//
//	public static String toUpperCaseFirstOne(String str) {
//		if (Character.isUpperCase(str.charAt(0)))
//			return str;
//		else
//			return (new StringBuilder()).append(Character.toUpperCase(str.charAt(0))).append(str.substring(1))
//					.toString();
//	}
//
//	private static MysqlTable getTableStruct(String className) throws ClassNotFoundException {
//		Class<?> cl = Class.forName(className);
//		DBTable dbtable = cl.getAnnotation(DBTable.class);
//		if (dbtable != null) {
//			String tableName = dbtable.name();
//			if (tableName.equals("") == false) {
//				MysqlTable mysqlTable = new MysqlTable();
//				mysqlTable.tableName = tableName.toLowerCase();
//				mysqlTable.className = className;
//				MethodAccess ma = MethodAccess.get(cl);
//				for (Field field : cl.getDeclaredFields()) {
//					Annotation[] annotations = field.getAnnotations();
//					if (annotations.length != 1) {
//						continue;
//					}
//					MysqlFiled mysqlFileld = new MysqlFiled();
//					String columnName = field.getName();
//					String getMethodName = "get" + toUpperCaseFirstOne(columnName);
//					mysqlFileld.getMethodIndex = ma.getIndex(getMethodName);
//					String setMethodName = "set" + toUpperCaseFirstOne(columnName);
//					mysqlFileld.setMethodIndex = ma.getIndex(setMethodName);
//					if (annotations[0] instanceof SQLBigInt) {
//						SQLBigInt sqlType = (SQLBigInt) annotations[0];
//						mysqlFileld.type = MysqlType.BIGINT;
//						mysqlFileld.length = sqlType.length();
//						mysqlFileld.constraints = getConstraints(sqlType.constraints());
//						mysqlTable.fields.put(columnName, mysqlFileld);
//					} else if (annotations[0] instanceof SQLDouble) {
//						SQLDouble sqlType = (SQLDouble) annotations[0];
//						mysqlFileld.type = MysqlType.DOUBLT;
//						mysqlFileld.constraints = getConstraints(sqlType.constraints());
//						mysqlTable.fields.put(columnName, mysqlFileld);
//					} else if (annotations[0] instanceof SQLInt) {
//						SQLInt sqlType = (SQLInt) annotations[0];
//						mysqlFileld.type = MysqlType.INT;
//						mysqlFileld.length = sqlType.length();
//						mysqlFileld.constraints = getConstraints(sqlType.constraints());
//						mysqlTable.fields.put(columnName, mysqlFileld);
//					} else if (annotations[0] instanceof SQLVarChar) {
//						SQLVarChar sqlType = (SQLVarChar) annotations[0];
//						mysqlFileld.type = MysqlType.VARCHAR;
//						mysqlFileld.length = sqlType.length();
//						mysqlFileld.constraints = getConstraints(sqlType.constraints());
//						mysqlTable.fields.put(columnName, mysqlFileld);
//					} else if (annotations[0] instanceof SQLMediumBlob) {
//						SQLMediumBlob sqlType = (SQLMediumBlob) annotations[0];
//						mysqlFileld.type = MysqlType.MEDIUMBLOB;
//						mysqlFileld.constraints = getConstraints(sqlType.constraints());
//						mysqlTable.fields.put(columnName, mysqlFileld);
//					} else {
//						Trace.warn("Mysql javaBean 有未知注解");
//					}
//				}
//				return mysqlTable;
//			}
//		}
//		return null;
//	}
//
//	private static MysqlConstraints getConstraints(Constraints constraints) { // 获取字段约束属性
//		MysqlConstraints myconsraints = new MysqlConstraints();
//		myconsraints.allowNull = constraints.allowNull();
//		myconsraints.primaryKey = constraints.primaryKey();
//		myconsraints.autoIncement = constraints.autoIncement();
//		return myconsraints;
//	}
}
