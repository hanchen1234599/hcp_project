package com.hc.test;

import java.sql.Connection;
import java.sql.SQLException;

import com.zaxxer.hikari.HikariDataSource;

public class Main7 {
	public static void main(String[] args) throws SQLException {
		HikariDataSource ds = null;
		@SuppressWarnings("null")
		Connection conn = ds.getConnection();
		conn.createStatement();
		//conn.prepareStatement(sql);
	}
}
