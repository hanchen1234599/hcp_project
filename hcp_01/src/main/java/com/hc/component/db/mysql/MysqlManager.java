package com.hc.component.db.mysql;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.ExecutorService;

import com.hc.component.db.mysql.async.MysqlFindResult;
import com.hc.component.db.mysql.async.MysqlInsertResult;
import com.hc.component.db.mysql.async.MysqlUpdateResult;
import com.hc.share.Manager;
import com.hc.share.exception.DbException;

public interface MysqlManager extends Manager<MysqlListener> {
	ResultSet find(String where, Object...args) throws SQLException, DbException;
	void findASync( MysqlFindResult callback, ExecutorService callbackExecutor, String where, Object...args );
	boolean update(String update, Object...args) throws SQLException, DbException;
	void updateASync(MysqlUpdateResult callback, ExecutorService callbackExecutor, String update, Object...args);
	boolean insert(String insert, Object...args) throws SQLException, DbException;
	void insertASync(MysqlInsertResult callback, ExecutorService callbackExecutor, String insert, Object...args);
}