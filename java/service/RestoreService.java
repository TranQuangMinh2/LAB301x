package com.trnqngmnh.library.service;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
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

	// Restore database from SQL dump
	public void restoreDatabase(String username, String password, String database, String backupFileName)
			throws SQLException {
		Connection connection = null;
		Statement statement = null;
		BufferedReader reader = null;

		try {
			// Load MySQL JDBC Driver
			Class.forName(DRIVER_CLASS);

			// Establish connection
			connection = DriverManager.getConnection(JDBC_URL + database, username, password);
			statement = connection.createStatement();

			// Read the SQL file
			String backupFilePath = BACKUP_PATH + backupFileName;
			reader = new BufferedReader(new FileReader(backupFilePath));
			StringBuilder sql = new StringBuilder();
			String line;

			// Read each line of the file and append it to the SQL command
			while ((line = reader.readLine()) != null) {
				// Skip comments or special MySQL commands
				if (line.startsWith("/*!") || line.startsWith("--")) {
					continue; // Skip this line
				}

				sql.append(line);

				// If a semicolon is found, it indicates the end of an SQL statement
				if (line.endsWith(";")) {
					// Execute the SQL statement
					statement.execute(sql.toString());
					// Reset the StringBuilder for the next SQL statement
					sql.setLength(0);
				}
			}

			System.out.println("Restore successful.");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			throw new SQLException("JDBC Driver not found.", e);
		} catch (IOException e) {
			e.printStackTrace();
			throw new SQLException("Error reading backup file.", e);
		} finally {
			if (reader != null) {
				try {
					reader.close();
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
}
