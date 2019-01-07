 package share.test.mysql;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.esotericsoftware.reflectasm.MethodAccess;

public final class MysqlTable {
	public String tableName = "";
	public String className = "";
	public MethodAccess ma = null;
	public Map<String, MysqlFiled> fields = new HashMap<String, MysqlFiled>();
	// 这里只留了 三个查询条件 
	public ArrayList<String> findList1 = new ArrayList<>();
	public ArrayList<String> findList2 = new ArrayList<>();
	public ArrayList<String> findList3 = new ArrayList<>();
	
	@Override
 	public String toString() {
		StringBuilder sb = new StringBuilder("Create Table " + tableName + "(");
		for(Entry<String, MysqlFiled> field : fields.entrySet()) {
			sb.append("\n    " + field.getKey() + " " + field.getValue() + ",");
		}
		return sb.substring(0, sb.length() - 1) +"\n) NGINE=InnoDB DEFAULT CHARSET=utf8;";
	}

}
