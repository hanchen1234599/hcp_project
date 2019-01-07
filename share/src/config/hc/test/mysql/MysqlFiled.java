package hc.test.mysql;

public final class MysqlFiled {
	public MysqlType type = null;
	public int length = 0;
	public int getMethodIndex = 0;
	public int setMethodIndex = 0;
	public MysqlConstraints constraints = null;
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		if(type == MysqlType.BIGINT) {
			sb.append("bigint(" + length + ")");
		}else if(type == MysqlType.DOUBLT) {
			sb.append("double");
		}else if(type == MysqlType.INT) {
			sb.append("int(" + length + ")");
		}else if(type == MysqlType.VARCHAR) {
			sb.append("varchar(" + length + ")");
		}else if(type == MysqlType.MEDIUMBLOB) {
			sb.append("mediumblob");
		}
		sb.append(constraints);
		return sb.toString();
	}
}
