package hc.mysql.desc;
/**
 * @author hc DBbean父类
 */
@Deprecated
public abstract class MysqlBean {
//	/**
//	 * 
//	 */
//	private static final long serialVersionUID = -879419458122971550L;
//	/**
//	 * @param managerInter     数据库管理器
//	 * @param callback         查询回调
//	 * @param callbackExecutor 执行器
//	 */
//	public void prepareFindASync(MysqlManager managerInter, BeanFindResult callback, ExecutorService callbackExecutor,
//			String where, Object... condition) { // 异步查询
//		MysqlManagerImpl manager = (MysqlManagerImpl) managerInter;
//		ExecutorService dbExec = manager.getExecute();
//		CompletableFuture.supplyAsync(() -> {
//			try {
//				return prepareFind(managerInter, where, condition);
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//			return null;
//		}, dbExec).thenAcceptAsync((data) -> {
//			callback.dealResult(data);
//		}, callbackExecutor);
//	}
//	/**
//	 * @param managerInter     数据库管理器
//	 * @param callback         查询回调
//	 * @param callbackExecutor 执行器
//	 * @param primaryKey       主键value
//	 */
//	public void findByPrimaryKeyASync(MysqlManager managerInter, BeanFindResult callback, ExecutorService callbackExecutor,
//			Object primaryKey) { // 异步查询
//		MysqlManagerImpl manager = (MysqlManagerImpl) managerInter;
//		ExecutorService dbExec = manager.getExecute();
//		CompletableFuture.supplyAsync(() -> {
//			try {
//				return findByPrimaryKey(managerInter, primaryKey);
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//			return null;
//		}, dbExec).thenAcceptAsync((data) -> {
//			callback.dealResult(data);
//		}, callbackExecutor);
//	}
//	/**
//	 * @param managerInter     数据库管理器
//	 * @param callback         插入回调
//	 * @param callbackExecutor 执行器
//	 */
//	public void insertASync(MysqlManager managerInter, BeanInsertResult callback, ExecutorService callbackExecutor) {
//		MysqlManagerImpl manager = (MysqlManagerImpl) managerInter;
//		ExecutorService dbExec = manager.getExecute();
//		CompletableFuture.supplyAsync(() -> {
//			try {
//				return insert(manager);
//			} catch (SQLException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			} catch (DbException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//			return null;
//		}, dbExec).thenAcceptAsync((data) -> {
//			callback.dealResult(data);
//		}, callbackExecutor);
//	}
//	/**
//	 * @param managerInter     数据库管理器
//	 * @param callback         更新结果 成功失败 回调
//	 * @param callbackExecutor 回调执行器
//	 */
//	public void updateASync(MysqlManager managerInter, BeanUpdateResult callback, ExecutorService callbackExecutor) {
//		MysqlManagerImpl manager = (MysqlManagerImpl) managerInter;
//		ExecutorService dbExec = manager.getExecute();
//		CompletableFuture.supplyAsync(() -> {
//			try {
//				return update(manager);
//			} catch (SQLException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			} catch (DbException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//			return false;
//		}, dbExec).thenAcceptAsync((data) -> {
//			callback.dealResult(data);
//		}, callbackExecutor);
//	}
//	/**
//	 * @param managerInter 数据库管理器
//	 * @return 插入结果 成功失败
//	 * @throws 数据库语句 执行异常
//	 * @throws 自定义异常
//	 */
//	public MysqlBean insert(MysqlManager managerInter) throws SQLException, DbException {
//		MysqlManagerImpl manager = (MysqlManagerImpl) managerInter;
//		String className = this.getClass().getName();
//		MysqlTable table = manager.getTableByClassName(className);
//		Connection conn = manager.getConnection();
//		// 这里查询全部数据 处理数据sql语句
//		StringBuilder sql = new StringBuilder("insert into");
//		sql.append(" " + table.tableName);
//		boolean isFrist = true;
//		ArrayList<MysqlFiled> list = new ArrayList<MysqlFiled>();
//		int count = 0;
//		StringBuilder keyStr = new StringBuilder("( ");
//		StringBuilder valueStr = new StringBuilder(" value(");
//		boolean withAutoIncementPrimaryKey = true;
//		MysqlFiled autoIncementPrimaryField = null;
//		for (Entry<String, MysqlFiled> field : table.fields.entrySet()) {
//			if (field.getValue().constraints.primaryKey == true) {
//				if (field.getValue().constraints.autoIncement == true)
//					withAutoIncementPrimaryKey = true;
//					autoIncementPrimaryField = field.getValue();
//					continue;
//			}
//			if (isFrist == true) {
//				keyStr.append(" ");
//				valueStr.append(" ");
//			} else {
//				keyStr.append(", ");
//				valueStr.append(", ");
//			}
//			keyStr.append(field.getKey());
//			valueStr.append("?");
//			list.add(field.getValue());
//			count = count + 1;
//			// }
//		}
//		keyStr.append(")");
//		valueStr.append(")");
//		sql.append(keyStr.toString());
//		sql.append(valueStr.toString());
//		sql.append(";");
//		Trace.debug(sql.toString());
//		PreparedStatement ps = conn.prepareStatement(sql.toString());
//		int length = list.size();
//		for (int index = 1; index <= length; index++) {
//			MysqlFiled field = list.get(index - 1);
//			if (field.type == MysqlType.INT) {
//				// int value = ps.getInt(index);
//				int value = (int) table.ma.invoke(this, field.getMethodIndex);
//				ps.setInt(index, value);
//			} else if (field.type == MysqlType.BIGINT) {
//				long value = (long) table.ma.invoke(this, field.getMethodIndex);
//				ps.setLong(index, value);
//			} else if (field.type == MysqlType.DOUBLT) {
//				double value = (double) table.ma.invoke(this, field.getMethodIndex);
//				ps.setDouble(index, value);
//			} else if (field.type == MysqlType.VARCHAR) {
//				String value = (String) table.ma.invoke(this, field.getMethodIndex);
//				ps.setString(index, value);
//			} else if (field.type == MysqlType.MEDIUMBLOB) {
//				byte[] value = (byte[]) table.ma.invoke(this, field.getMethodIndex);
//				ps.setBlob(index, new ByteArrayInputStream(value));
//			} else {
//				Trace.error("class:" + className + " 类型定义错误");
//				throw new DbException("class:" + className + " 类型定义错误");
//			}
//		}
//		
//		if(withAutoIncementPrimaryKey == false) {
//			Statement stm  = conn.createStatement();
//			ResultSet rs = stm.executeQuery("SELECT LAST_INSERT_ID()");
//			if(rs.next()) {
//				if (autoIncementPrimaryField.type == MysqlType.INT) {
//					// int value = ps.getInt(index);
//					int value = rs.getInt(1);
//					table.ma.invoke(this, autoIncementPrimaryField.setMethodIndex, value);
//				} else if (autoIncementPrimaryField.type == MysqlType.BIGINT) {
//					long value = rs.getLong(1);
//					table.ma.invoke(this, autoIncementPrimaryField.setMethodIndex, value);
//				} else if (autoIncementPrimaryField.type == MysqlType.DOUBLT) {
//					double value = rs.getDouble(1);
//					table.ma.invoke(this, autoIncementPrimaryField.setMethodIndex, value);
//				} else if (autoIncementPrimaryField.type == MysqlType.VARCHAR) {
//					String value = rs.getString(1);
//					table.ma.invoke(this, autoIncementPrimaryField.setMethodIndex, value);
//				} else if (autoIncementPrimaryField.type == MysqlType.MEDIUMBLOB) {
//					Blob blob = rs.getBlob(1);
//					byte[] value = blob.getBytes(1, (int) blob.length());
//					table.ma.invoke(this, autoIncementPrimaryField.setMethodIndex, value);
//				} else {
//					Trace.error("class:" + className + " 类型定义错误");
//					throw new DbException("class:" + className + " 类型定义错误");
//				}
//			}
//		}
//		return this;
//	}
//	/**
//	 * @param managerInter 数据库管理器
//	 * @return 插入结果 成功失败
//	 * @throws 数据库语句 执行异常
//	 * @throws 自定义异常
//	 */
//	public boolean update(MysqlManager managerInter) throws DbException, SQLException {
//		MysqlManagerImpl manager = (MysqlManagerImpl) managerInter;
//		String className = this.getClass().getName();
//		MysqlTable table = manager.getTableByClassName(className);
//		Connection conn = manager.getConnection();
//		// 这里查询全部数据 处理数据sql语句
//		StringBuilder sql = new StringBuilder("update ");
//		sql.append(table.tableName);
//		sql.append(" set");
//		boolean isFrist = true;
//		ArrayList<MysqlFiled> list = new ArrayList<MysqlFiled>();
//		int count = 0;
//		String where = "";
//		MysqlFiled primaryKeyField = null;
//		for (Entry<String, MysqlFiled> field : table.fields.entrySet()) {
//			if (field.getValue().constraints.primaryKey == true) {
//				where = "where " + field.getKey() + "= ?";
//				primaryKeyField = field.getValue();
//				continue;
//			}
//			if (isFrist == true) {
//				sql.append(" ");
//			} else {
//				sql.append(", ");
//			}
//			sql.append(field.getKey());
//			sql.append("=?");
//			list.add(field.getValue());
//			count = count + 1;
//		}
//		if (where == "") {
//			throw new DbException(className + "没有设立主键");
//		}
//		sql.append(where);
//		sql.append(";");
//		Trace.debug(sql.toString());
//		PreparedStatement ps = conn.prepareStatement(sql.toString());
//		int length = list.size();
//		list.add(primaryKeyField);
//		for (int index = 1; index <= length; index++) {
//			MysqlFiled field = list.get(index - 1);
//			if (field.type == MysqlType.INT) {
//				// int value = ps.getInt(index);
//				int value = (int) table.ma.invoke(this, field.getMethodIndex);
//				ps.setInt(index, value);
//			} else if (field.type == MysqlType.BIGINT) {
//				long value = (long) table.ma.invoke(this, field.getMethodIndex);
//				ps.setLong(index, value);
//			} else if (field.type == MysqlType.DOUBLT) {
//				double value = (double) table.ma.invoke(this, field.getMethodIndex);
//				ps.setDouble(index, value);
//			} else if (field.type == MysqlType.VARCHAR) {
//				String value = (String) table.ma.invoke(this, field.getMethodIndex);
//				ps.setString(index, value);
//			} else if (field.type == MysqlType.MEDIUMBLOB) {
//				byte[] value = (byte[]) table.ma.invoke(this, field.getMethodIndex);
//				ps.setBlob(index, new ByteArrayInputStream(value));
//			} else {
//				Trace.error("class:" + className + " 类型定义错误");
//				throw new DbException("class:" + className + " 类型定义错误");
//			}
//		}
//		return ps.execute();
//	}
//	/**
//	 * @param managerInter     数据库管理器
//	 * @param callback         回调
//	 * @param where            查询条件
//	 * @param callback         查询回调
//	 * @param callbackExecutor 回调执行器
//	 */
//	@Deprecated
//	public void findASync(MysqlManager managerInter, BeanFindResult callback, ExecutorService callbackExecutor,
//			String where) { // 异步查询
//		MysqlManagerImpl manager = (MysqlManagerImpl) managerInter;
//		ExecutorService dbExec = manager.getExecute();
//		CompletableFuture.supplyAsync(() -> {
//			try {
//				return find(manager, where);
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//			return null;
//		}, dbExec).thenAcceptAsync((data) -> {
//			callback.dealResult(data);
//		}, callbackExecutor);
//	}
//	/**
//	 * @param managerInter     数据库管理器
//	 * @param 主键值            查询条件
//	 */
//	public MysqlBean findByPrimaryKey(MysqlManager managerInter, Object primaryKey) throws SQLException, DbException {
//		MysqlManagerImpl manager = (MysqlManagerImpl) managerInter;
//		String className = this.getClass().getName();
//		MysqlTable table = manager.getTableByClassName(className);
//		Connection conn = manager.getConnection();
//		// 这里查询全部数据 处理数据sql语句
//		StringBuilder sql = new StringBuilder("select");
//		boolean isFrist = true;
//		ArrayList<MysqlFiled> list = new ArrayList<MysqlFiled>();
//		String where = "where ";
//		int count = 0;
//		MysqlFiled primaryKeyField = null;
//		for (Entry<String, MysqlFiled> field : table.fields.entrySet()) {
//			if (field.getValue().constraints.primaryKey == true) {
//				where = where + field.getKey() + " = ? ";
//				primaryKeyField = field.getValue();
//			}
//			if (isFrist == true) {
//				sql.append(" ");
//			} else {
//				sql.append(", ");
//			}
//			sql.append(field.getKey());
//			list.add(field.getValue());
//			count = count + 1;
//		}
//		sql.append(" from " + table.tableName + " ");
//		sql.append(where);
//		sql.append(";");
//		Trace.debug(sql.toString());
//		PreparedStatement ps = conn.prepareStatement(sql.toString());
//		if (primaryKeyField.type == MysqlType.INT) {
//			// int value = ps.getInt(index);
//			int value = (int) table.ma.invoke(this, primaryKeyField.getMethodIndex);
//			ps.setInt(1, value);
//		} else if (primaryKeyField.type == MysqlType.BIGINT) {
//			long value = (long) table.ma.invoke(this, primaryKeyField.getMethodIndex);
//			ps.setLong(1, value);
//		} else if (primaryKeyField.type == MysqlType.DOUBLT) {
//			double value = (double) table.ma.invoke(this, primaryKeyField.getMethodIndex);
//			ps.setDouble(1, value);
//		} else if (primaryKeyField.type == MysqlType.VARCHAR) {
//			String value = (String) table.ma.invoke(this, primaryKeyField.getMethodIndex);
//			ps.setString(1, value);
//		} else if (primaryKeyField.type == MysqlType.MEDIUMBLOB) {
//			byte[] value = (byte[]) table.ma.invoke(this, primaryKeyField.getMethodIndex);
//			ps.setBlob(1, new ByteArrayInputStream(value));
//		} else {
//			Trace.error("class:" + className + " 类型定义错误");
//			throw new DbException("class:" + className + " 类型定义错误");
//		}
//		// Statement stm = conn.createStatement();
//		ResultSet rs = ps.executeQuery();
//		if (rs.next() == false)// 这里支持一条数据查询
//			return null;
//		for (int index = 1; index <= count; index++) {
//			MysqlFiled field = list.get(index - 1);
//			if (field.type == MysqlType.INT) {
//				int value = rs.getInt(index);
//				table.ma.invoke(this, field.setMethodIndex, value);
//			} else if (field.type == MysqlType.BIGINT) {
//				long value = rs.getLong(index);
//				table.ma.invoke(this, field.setMethodIndex, value);
//			} else if (field.type == MysqlType.DOUBLT) {
//				double value = rs.getDouble(index);
//				table.ma.invoke(this, field.setMethodIndex, value);
//			} else if (field.type == MysqlType.VARCHAR) {
//				String value = rs.getString(index);
//				table.ma.invoke(this, field.setMethodIndex, value);
//			} else if (field.type == MysqlType.MEDIUMBLOB) {
//				Blob blob = rs.getBlob(index);
//				byte[] value = blob.getBytes(1, (int) blob.length());
//				table.ma.invoke(this, field.setMethodIndex, value);
//			} else {
//				Trace.error("class:" + className + " 类型定义错误");
//				throw new DbException("class:" + className + " 类型定义错误");
//			}
//		}
//		return this;
//	}
//	/**
//	 * @param managerInter     数据库管理器
//	 * @param where            查询条件
//	 * @param condition        查询条件值
//	 */
//	public MysqlBean prepareFind(MysqlManager managerInter, String where, Object... condition)
//			throws SQLException, DbException { // 同步查询 修改自己本身
//		MysqlManagerImpl manager = (MysqlManagerImpl) managerInter;
//		String className = this.getClass().getName();
//		MysqlTable table = manager.getTableByClassName(className);
//		Connection conn = manager.getConnection();
//		// 这里查询全部数据 处理数据sql语句
//		StringBuilder sql = new StringBuilder("select");
//		boolean isFrist = true;
//		ArrayList<MysqlFiled> list = new ArrayList<MysqlFiled>();
//		int count = 0;
//		for (Entry<String, MysqlFiled> field : table.fields.entrySet()) {
//			if (isFrist == true) {
//				sql.append(" ");
//			} else {
//				sql.append(", ");
//			}
//			sql.append(field.getKey());
//			list.add(field.getValue());
//			count = count + 1;
//		}
//		sql.append(" from " + table.tableName + " ");
//		sql.append(where);
//		sql.append(";");
//		int whereIndex = 0;
//		int questionTime = 0;
//		while (true) {
//			if (where.indexOf("?", whereIndex) == -1) {
//				break;
//			} else {
//				questionTime++;
//				whereIndex = where.indexOf("?", whereIndex) + 1;
//			}
//		}
//		Trace.debug(sql.toString());
//		if (questionTime != condition.length)
//			throw new DbException("class:" + className + " " + where + " 条件对不上");
//		// Statement stm = conn.createStatement();
//		ResultSet rs = null;
//		if (condition.length > 0) {
//			Trace.debug(sql.toString());
//			PreparedStatement ps = conn.prepareStatement(sql.toString());
//			for (int i = 0; i < condition.length; i++) {
//				Object obj = condition[i];
//				if (obj instanceof Integer) {
//					int value = (Integer) obj;
//					ps.setInt(i + 1, value);
//				} else if (obj instanceof Long) {
//					long value = (Long) obj;
//					ps.setLong(i + 1, value);
//				} else if (obj instanceof Double) {
//					double value = (Double) obj;
//					ps.setDouble(i + 1, value);
//				} else if (obj instanceof String) {
//					String value = (String) obj;
//					ps.setString(i + 1, value);
//				} else {
//					Trace.error("class:" + className + " 条件查询类型错误");
//					throw new DbException("class:" + className + " 条件查询类型错误");
//				}	
//			}
//			rs = ps.executeQuery();
//		} else {
//			Statement stm = conn.createStatement();
//			rs = stm.executeQuery(sql.toString());
//		}
//
//		if (rs.next() == false)// 这里支持一条数据查询
//			return null;
//		for (int index = 1; index <= count; index++) {
//			MysqlFiled field = list.get(index - 1);
//			if (field.type == MysqlType.INT) {
//				int value = rs.getInt(index);
//				table.ma.invoke(this, field.setMethodIndex, value);
//			} else if (field.type == MysqlType.BIGINT) {
//				long value = rs.getLong(index);
//				table.ma.invoke(this, field.setMethodIndex, value);
//			} else if (field.type == MysqlType.DOUBLT) {
//				double value = rs.getDouble(index);
//				table.ma.invoke(this, field.setMethodIndex, value);
//			} else if (field.type == MysqlType.VARCHAR) {
//				String value = rs.getString(index);
//				table.ma.invoke(this, field.setMethodIndex, value);
//			} else if (field.type == MysqlType.MEDIUMBLOB) {
//				Blob blob = rs.getBlob(index);
//				byte[] value = blob.getBytes(1, (int) blob.length());
//				table.ma.invoke(this, field.setMethodIndex, value);
//			} else {
//				Trace.error("class:" + className + " 类型定义错误");
//				throw new DbException("class:" + className + " 类型定义错误");
//			}
//		}
//		return this;
//	}
//	/**
//	 * @param managerInter     数据库管理器
//	 * @param where            查询条件
//	 */
//	@Deprecated
//	public MysqlBean find(MysqlManager managerInter, String where) throws SQLException, DbException { // 同步查询 修改自己本身
//		MysqlManagerImpl manager = (MysqlManagerImpl) managerInter;
//		String className = this.getClass().getName();
//		MysqlTable table = manager.getTableByClassName(className);
//		Connection conn = manager.getConnection();
//		// 这里查询全部数据 处理数据sql语句
//		StringBuilder sql = new StringBuilder("select");
//		boolean isFrist = true;
//		ArrayList<MysqlFiled> list = new ArrayList<MysqlFiled>();
//		int count = 0;
//		for (Entry<String, MysqlFiled> field : table.fields.entrySet()) {
//			if (isFrist == true) {
//				sql.append(" ");
//			} else {
//				sql.append(", ");
//			}
//			sql.append(field.getKey());
//			list.add(field.getValue());
//			count = count + 1;
//		}
//		sql.append(" from " + table.tableName + " ");
//		sql.append(where);
//		sql.append(";");
//		Trace.debug(sql.toString());
//		Statement stm = conn.createStatement();
//		ResultSet rs = stm.executeQuery(sql.toString());
//		if (rs.next() == false)// 这里支持一条数据查询
//			return null;
//		for (int index = 1; index <= count; index++) {
//			MysqlFiled field = list.get(index - 1);
//			if (field.type == MysqlType.INT) {
//				int value = rs.getInt(index);
//				table.ma.invoke(this, field.setMethodIndex, value);
//			} else if (field.type == MysqlType.BIGINT) {
//				long value = rs.getLong(index);
//				table.ma.invoke(this, field.setMethodIndex, value);
//			} else if (field.type == MysqlType.DOUBLT) {
//				double value = rs.getDouble(index);
//				table.ma.invoke(this, field.setMethodIndex, value);
//			} else if (field.type == MysqlType.VARCHAR) {
//				String value = rs.getString(index);
//				table.ma.invoke(this, field.setMethodIndex, value);
//			} else if (field.type == MysqlType.MEDIUMBLOB) {
//				Blob blob = rs.getBlob(index);
//				byte[] value = blob.getBytes(1, (int) blob.length());
//				table.ma.invoke(this, field.setMethodIndex, value);
//			} else {
//				Trace.error("class:" + className + " 类型定义错误");
//				throw new DbException("class:" + className + " 类型定义错误");
//			}
//		}
//		return this;
//	}
}
