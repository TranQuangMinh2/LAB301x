package com.trnqngmnh.library.service;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class BackupService {
	private static final String JDBC_URL = "jdbc:mysql://localhost:3306/";
	private static final String DRIVER_CLASS = "com.mysql.cj.jdbc.Driver";
	private static final String BACKUP_PATH = "C:/ProgramData/MySQL/MySQL Server 8.0/Uploads/";

	// Backup lấy dump SQL
	public void backupDatabase(String username, String password, String database, String tableName,
			String backupFileName) throws SQLException {
		Connection connection = null;
		Statement statement = null;
		BufferedWriter writer = null;

		try {
			// Load MySQL JDBC Driver
			Class.forName(DRIVER_CLASS);

			// Establish connection
			connection = DriverManager.getConnection(JDBC_URL + database, username, password);
			statement = connection.createStatement();

			// Create backup file writer
			String backupFilePath = BACKUP_PATH + backupFileName;
			writer = new BufferedWriter(new FileWriter(backupFilePath));

			// Write configuration settings
			writer.write("/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;\n");
			writer.write("/*!40101 SET NAMES utf8 */;\n");
			writer.write("/*!50503 SET NAMES utf8mb4 */;\n");
			writer.write("/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;\n");
			writer.write("/*!40103 SET TIME_ZONE='+00:00' */;\n");
			writer.write("/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;\n");
			writer.write("/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;\n");
			writer.write("/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;\n");

			// Get table structure (CREATE TABLE IF NOT EXISTS statement)
			String showCreateTableSQL = "SHOW CREATE TABLE " + tableName;
			ResultSet rs = statement.executeQuery(showCreateTableSQL);
			if (rs.next()) {
				String createTableSQL = rs.getString(2);
				// Replace "CREATE TABLE" with "CREATE TABLE IF NOT EXISTS"
				createTableSQL = createTableSQL.replace("CREATE TABLE", "CREATE TABLE IF NOT EXISTS") + ";\n\n";
				writer.write("-- Dumping structure for table " + tableName + "\n");
				writer.write(createTableSQL);
			}

			// Add DELETE FROM statement before inserting data
			writer.write("-- Deleting existing data from table " + tableName + "\n");
			writer.write("TRUNCATE TABLE " + tableName + ";\n\n");

			// Get table data (INSERT INTO statements)
			String selectDataSQL = "SELECT * FROM " + tableName;
			rs = statement.executeQuery(selectDataSQL);
			ResultSetMetaData metaData = rs.getMetaData();
			int columnCount = metaData.getColumnCount();

			writer.write("-- Dumping data for table " + tableName + "\n");
			while (rs.next()) {
				StringBuilder insertSQL = new StringBuilder("INSERT INTO " + tableName + " VALUES (");
				for (int i = 1; i <= columnCount; i++) {
					Object value = rs.getObject(i);
					String columnType = metaData.getColumnTypeName(i);
					if (value == null) {
						insertSQL.append("NULL");
					} else if (value instanceof String) {
						insertSQL.append("'").append(value.toString().replace("'", "\\'")).append("'");
					} else if (value instanceof Boolean && "BIT".equalsIgnoreCase(columnType)) {
						System.out.println("value BIT: " + value);
						insertSQL.append(((Boolean) value) ? "1" : "0");
					} else if ("DATETIME".equalsIgnoreCase(columnType)) {
						insertSQL.append("'").append(value.toString().replace("'", "\\'")).append("'");
					} else {
						insertSQL.append(value.toString());
					}
					if (i < columnCount) {
						insertSQL.append(", ");
					}
				}
				insertSQL.append(");\n");
				writer.write(insertSQL.toString());
			}

			System.out.println("Backup successful.");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			throw new SQLException("JDBC Driver not found.", e);
		} catch (IOException e) {
			e.printStackTrace();
			throw new SQLException("Error writing to backup file.", e);
		} finally {
			if (writer != null) {
				try {
					writer.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if (statement != null) {
				statement.close();
			}
			if (connection != null) {
				connection.close();
			}
		}
	}

	// Backup lấy dump SQL
	public void backupDatabaseAuto(String username, String password, String database, String tableName,
			String backupFileName) throws SQLException {
		Connection connection = null;
		Statement statement = null;
		BufferedWriter writer = null;

		try {
			// Load MySQL JDBC Driver
			Class.forName(DRIVER_CLASS);

			// Establish connection
			connection = DriverManager.getConnection(JDBC_URL + database, username, password);
			statement = connection.createStatement();

			// Create backup file writer
			String backupFilePath = BACKUP_PATH + backupFileName;
			writer = new BufferedWriter(new FileWriter(backupFilePath));

			// Write configuration settings
			writer.write("/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;\n");
			writer.write("/*!40101 SET NAMES utf8 */;\n");
			writer.write("/*!50503 SET NAMES utf8mb4 */;\n");
			writer.write("/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;\n");
			writer.write("/*!40103 SET TIME_ZONE='+00:00' */;\n");
			writer.write("/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;\n");
			writer.write("/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;\n");
			writer.write("/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;\n\n");

			// Get table structure (CREATE TABLE IF NOT EXISTS statement)
			String showCreateTableSQL = "SHOW CREATE TABLE " + tableName;
			ResultSet rs = statement.executeQuery(showCreateTableSQL);
			if (rs.next()) {
				String createTableSQL = rs.getString(2);
				// Replace "CREATE TABLE" with "CREATE TABLE IF NOT EXISTS"
				createTableSQL = createTableSQL.replace("CREATE TABLE", "CREATE TABLE IF NOT EXISTS") + ";\n\n";
				writer.write("-- Dumping structure for table " + tableName + "\n");
				writer.write(createTableSQL);
			}

			// Add DELETE FROM statement before inserting data
			writer.write("-- Deleting existing data from table " + tableName + "\n");
			writer.write("TRUNCATE TABLE " + tableName + ";\n\n");

			// Get table data (INSERT INTO statements)
			String selectDataSQL = "SELECT * FROM " + tableName;
			rs = statement.executeQuery(selectDataSQL);
			ResultSetMetaData metaData = rs.getMetaData();
			int columnCount = metaData.getColumnCount();

			writer.write("-- Dumping data for table " + tableName + "\n");
			while (rs.next()) {
				StringBuilder insertSQL = new StringBuilder("INSERT INTO " + tableName + " VALUES (");
				for (int i = 1; i <= columnCount; i++) {
					Object value = rs.getObject(i);
					String columnType = metaData.getColumnTypeName(i);
					if (value == null) {
						insertSQL.append("NULL");
					} else if (value instanceof String) {
						// Escape single quotes in string values
						insertSQL.append("'").append(value.toString().replace("'", "\\'")).append("'");
					} else if (value instanceof Boolean && "BIT".equalsIgnoreCase(columnType)) {
						System.out.println("value BIT: " + value);
						insertSQL.append(((Boolean) value) ? "1" : "0");
					} else if ("DATETIME".equalsIgnoreCase(columnType)) {
						insertSQL.append("'").append(value.toString().replace("'", "\\'")).append("'");
					} else {
						insertSQL.append(value.toString());
					}
					if (i < columnCount) {
						insertSQL.append(", ");
					}
				}
				insertSQL.append(");\n");
				writer.write(insertSQL.toString());
			}

			// Restore configuration settings

			System.out.println("Backup successful.");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			throw new SQLException("JDBC Driver not found.", e);
		} catch (IOException e) {
			e.printStackTrace();
			throw new SQLException("Error writing to backup file.", e);
		} finally {
			if (writer != null) {
				try {
					writer.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (statement != null) {
				statement.close();
			}
			if (connection != null) {
				connection.close();
			}
		}
	}

	// Schedule backup task every minute
	public void scheduleAutoBackup(String username, String password, String database, String[] tableNames) {
		ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

		scheduler.scheduleAtFixedRate(() -> {
			String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
			for (String tableName : tableNames) {
				try {
					// Generate a unique file name for each backup
					String backupFileName = "backup_" + tableName + "_" + timestamp + ".sql";
					backupDatabaseAuto(username, password, database, tableName, backupFileName);
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}, 0, 1, TimeUnit.DAYS); // Runs every 1 minute
	}

	// Schedule backup task every minute
	@Scheduled(fixedRate = 24 * 60 * 60 * 1000) // 24 hours (1 day)
	public void scheduleAutoBackup() {
		String username = "root"; // Replace with your username
		String password = "123456"; // Replace with your password
		String database = "library_manage"; // Replace with your database name
		String[] tablesToBackup = { "brand", "category", "badword", "user", "product", "product_size", "document",
				"product_image" };

		String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
		for (String tableName : tablesToBackup) {
			try {
				// Generate a unique file name for each backup
				String backupFileName = "backup_" + tableName + "_" + timestamp + ".sql";
				backupDatabaseAuto(username, password, database, tableName, backupFileName);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
}
//writer.write("\n/*!40103 SET TIME_ZONE=IFNULL(@OLD_TIME_ZONE, 'system') */;\n");
//writer.write("/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;\n");
//writer.write("/*!40014 SET FOREIGN_KEY_CHECKS=IFNULL(@OLD_FOREIGN_KEY_CHECKS, 1) */;\n");
//writer.write("/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;\n");
//writer.write("/*!40111 SET SQL_NOTES=IFNULL(@OLD_SQL_NOTES, 1) */;\n");
