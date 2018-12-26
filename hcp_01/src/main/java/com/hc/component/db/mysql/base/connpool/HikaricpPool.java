package com.hc.component.db.mysql.base.connpool;

import java.sql.Connection;
import java.sql.SQLException;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

public class HikaricpPool {
	private HikariDataSource dataSource = null;
	public HikaricpPool(String dbConfigPath) {
		HikariConfig config = new HikariConfig(dbConfigPath);
		this.dataSource = new HikariDataSource(config);
	}

	public Connection getConnection() throws SQLException {
		return this.dataSource.getConnection();
	}
	public void close() {
		this.dataSource.close();
	}
}
