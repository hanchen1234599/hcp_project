package com.hc.component.db.mysql.base.desc;

public class MysqlConstraints {
	public boolean allowNull = true;
	public boolean primaryKey = false;
	public boolean autoIncement = false;
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		if(allowNull == false)
			sb.append(" NOT NULL");
		if(primaryKey == true)
			sb.append(" PRIMARY KEY");
		if(autoIncement == true)
			sb.append(" AUTO_INCREMENT");
		return sb.toString();
	}
	
}
