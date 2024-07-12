//package com.trnqngmnh.library;
//
//import java.sql.Connection;
//import java.sql.DriverManager;
//import java.sql.SQLException;
//import java.sql.Statement;
//
//import org.springframework.stereotype.Service;
//
//@Service
//public class RestoreService {
//
//	private static final String JDBC_URL = "jdbc:mysql://localhost:3306/";
//	private static final String DRIVER_CLASS = "com.mysql.cj.jdbc.Driver";
//
//	public void restoreDatabase(String username, String password, String database, String tableName,
//			String backupFilePath) throws SQLException {
//		Connection connection = null;
//		Statement statement = null;
//
//		try {
//			// Load MySQL JDBC Driver
//			Class.forName(DRIVER_CLASS);
//
//			// Establish connection
//			connection = DriverManager.getConnection(JDBC_URL + database, username, password);
//			statement = connection.createStatement();
//
//			// Create SQL command
//			String sql = "LOAD DATA INFILE '" + backupFilePath.replace("\\", "/") + "' " + "INTO TABLE " + tableName
//					+ " FIELDS TERMINATED BY ',' ENCLOSED BY '\"' " + "LINES TERMINATED BY '\\n'";
//
//			// Execute SQL command
//			statement.execute(sql);
//			System.out.println("Restore successful.");
//		} catch (ClassNotFoundException e) {
//			e.printStackTrace();
//			throw new SQLException("JDBC Driver not found.", e);
//		} finally {
//			if (statement != null) {
//				statement.close();
//			}
//			if (connection != null) {
//				connection.close();
//			}
//		}
//	}
//}
package com.trnqngmnh.library;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import org.springframework.stereotype.Service;

@Service
public class RestoreService {

	private static final String JDBC_URL = "jdbc:mysql://localhost:3306/";
	private static final String DRIVER_CLASS = "com.mysql.cj.jdbc.Driver";
	private static final String BACKUP_PATH = "C:/ProgramData/MySQL/MySQL Server 8.0/Uploads/";

	public void restoreDatabase(String username, String password, String database, String tableName,
			String backupFileName) throws SQLException {
		Connection connection = null;
		Statement statement = null;

		try {
			// Load MySQL JDBC Driver
			Class.forName(DRIVER_CLASS);

			// Establish connection
			connection = DriverManager.getConnection(JDBC_URL + database, username, password);
			statement = connection.createStatement();

			// Create SQL command
			String backupFilePath = BACKUP_PATH + backupFileName;
			String sql = "LOAD DATA INFILE '" + backupFilePath.replace("\\", "/") + "' " + "INTO TABLE " + tableName
					+ " FIELDS TERMINATED BY ',' ENCLOSED BY '\"' " + "LINES TERMINATED BY '\\n'";

			// Execute SQL command
			statement.execute(sql);
			System.out.println("Restore successful.");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			throw new SQLException("JDBC Driver not found.", e);
		} finally {
			if (statement != null) {
				statement.close();
			}
			if (connection != null) {
				connection.close();
			}
		}
	}
}
