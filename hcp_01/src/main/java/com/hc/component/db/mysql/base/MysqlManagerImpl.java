package com.hc.component.db.mysql.base;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.hc.component.db.mysql.MysqlListener;
import com.hc.component.db.mysql.MysqlManager;
import com.hc.component.db.mysql.base.connpool.HikaricpPool;
import com.hc.component.db.mysql.async.MysqlFindResult;
import com.hc.component.db.mysql.async.MysqlInsertResult;
import com.hc.component.db.mysql.async.MysqlUpdateResult;
import com.hc.component.db.mysql.base.desc.MysqlDB;
import com.hc.component.db.mysql.base.desc.MysqlTable;
import com.hc.share.exception.DbException;
import com.hc.share.util.MysqlUtil;
import com.hc.share.util.Trace;

public class MysqlManagerImpl implements MysqlManager {
	private MysqlDB db = null;
	private HashMap<String, MysqlTable> tables = null;
	private MysqlListener listener = null;
	private HikaricpPool hikaricp = null;
	private ExecutorService excutorPool = null;

	private int nThreads = 1;
	private String packetPath = "";
	private String dbConfigPath = "";

	public MysqlManagerImpl(String packetPath, int nThreads, String dbConfigPath) {
		this.nThreads = nThreads;
		this.packetPath = packetPath;
		this.dbConfigPath = dbConfigPath;
	}

	@Override
	public void registListener(MysqlListener listener) {
		this.listener = listener;
	}

	@Override
	public void open() throws Exception {
		this.hikaricp = new HikaricpPool(this.dbConfigPath);
		this.tables = new HashMap<>();
		this.db = MysqlUtil.getMysqlDb(packetPath);
		for (Entry<String, MysqlTable> table : db.tables.entrySet()) {
			this.tables.put(table.getValue().className, table.getValue());
		}
		this.excutorPool = Executors.newFixedThreadPool(this.nThreads);
		Trace.logger.info("mysql connect success");
	}

	@Override
	public void close() {
		this.db = null;
		this.tables = null;
		this.hikaricp.close();
		this.excutorPool.shutdown();
		this.hikaricp = null;
		this.excutorPool = null;
		this.listener.onDestory(this);
	}

	protected MysqlTable getTableByClassName(String className) {
		return tables.get(className);
	}

	protected MysqlTable getTableByTableName(String tableName) {
		return db.tables.get(tableName);
	}

	protected Connection getConnection() {
		try {
			return this.hikaricp.getConnection();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	protected void execute(Runnable command) {
		this.excutorPool.execute(command);
	}

	protected ExecutorService getExecute() {
		return this.excutorPool;
	}

	@Override
	public ResultSet find(String where, Object... args) throws SQLException, DbException {
		Connection conn = getConnection();
		ResultSet rs = null;
		if (args.length > 0) {
			Trace.logger.info(where);
			PreparedStatement ps = conn.prepareStatement(where);
			for (int i = 0; i < args.length; i++) {
				Object obj = args[i];
				if (obj instanceof Integer) {
					int value = (Integer) obj;
					ps.setInt(i + 1, value);
				} else if (obj instanceof Long) {
					long value = (Long) obj;
					ps.setLong(i + 1, value);
				} else if (obj instanceof Double) {
					double value = (Double) obj;
					ps.setDouble(i + 1, value);
				} else if (obj instanceof String) {
					String value = (String) obj;
					ps.setString(i + 1, value);
				} else {
					Trace.error("where:" + where + " 条件查询类型错误");
					throw new DbException("where:" + where + " 条件查询类型错误");
				}
			}
			rs = ps.executeQuery();
			ps.close();
		} else {
			Statement stm = conn.createStatement();
			rs = stm.executeQuery(where);
			stm.close();
		}
		conn.close();
		return rs;
	}
	@Override
	public void findASync(MysqlFindResult callback, ExecutorService callbackExecutor, String where, Object... args) {
		CompletableFuture.supplyAsync(() -> {
			try {
				return find(where, args);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}, this.excutorPool).thenAcceptAsync((data) -> {
			callback.dealResult(data);
		}, callbackExecutor);
	}
	@Override
	public boolean update(String update, Object... args) throws SQLException, DbException {
		Connection conn = this.hikaricp.getConnection();		
		PreparedStatement ps = conn.prepareStatement(update);
		for (int i = 0; i < args.length; i++) {
			Object obj = args[i];
			if (obj instanceof Integer) {
				int value = (Integer) obj;
				ps.setInt(i + 1, value);
			} else if (obj instanceof Long) {
				long value = (Long) obj;
				ps.setLong(i + 1, value);
			} else if (obj instanceof Double) {
				double value = (Double) obj;
				ps.setDouble(i + 1, value);
			} else if (obj instanceof String) {
				String value = (String) obj;
				ps.setString(i + 1, value);
			} else {
				Trace.error("update:" + update + " 条件update类型错误");
				throw new DbException("update:" + update + " 条件update错误");
			}
		}
		boolean result = ps.execute();
		ps.close();
		conn.close();
		return result;
	}
	@Override
	public void updateASync(MysqlUpdateResult callback, ExecutorService callbackExecutor, String update, Object... args) {
		CompletableFuture.supplyAsync(() -> {
			try {
				return update(update, args);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}, this.excutorPool).thenAcceptAsync((data) -> {
			callback.dealResult(data);
		}, callbackExecutor);
	}
	@Override
	public boolean insert(String insert, Object... args) throws SQLException, DbException {
		Connection conn = this.hikaricp.getConnection();		
		PreparedStatement ps = conn.prepareStatement(insert);
		for (int i = 0; i < args.length; i++) {
			Object obj = args[i];
			if (obj instanceof Integer) {
				int value = (Integer) obj;
				ps.setInt(i + 1, value);
			} else if (obj instanceof Long) {
				long value = (Long) obj;
				ps.setLong(i + 1, value);
			} else if (obj instanceof Double) {
				double value = (Double) obj;
				ps.setDouble(i + 1, value);
			} else if (obj instanceof String) {
				String value = (String) obj;
				ps.setString(i + 1, value);
			} else {
				Trace.error("insert:" + insert + " 条件insert类型错误");
				throw new DbException("insert:" + insert + " 条件insert错误");
			}
		}
		boolean result = ps.execute();
		ps.close();
		conn.close();
		return result;
	}
	@Override
	public void insertASync(MysqlInsertResult callback, ExecutorService callbackExecutor, String insert, Object... args) {
		CompletableFuture.supplyAsync(() -> {
			try {
				return update(insert, args);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}, this.excutorPool).thenAcceptAsync((data) -> {
			callback.dealResult(data);
		}, callbackExecutor);
	}
}
