package com.trnqngmnh.library;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.trnqngmnh.library.service.BackupService;

class BackupServiceTest {

	private BackupService backupService;

	@Mock
	private Connection mockConnection;

	@Mock
	private Statement mockStatement;

	@Mock
	private ResultSet mockResultSet;

	@Mock
	private ResultSetMetaData mockMetaData;

	@Mock
	private BufferedWriter mockWriter;

	@BeforeEach
	void setUp() throws Exception {
		MockitoAnnotations.openMocks(this);
		backupService = new BackupService();

		// Mocking ResultSet metadata
		when(mockResultSet.getMetaData()).thenReturn(mockMetaData);
		when(mockMetaData.getColumnCount()).thenReturn(2); // Example with 2 columns
		when(mockMetaData.getColumnTypeName(anyInt())).thenReturn("VARCHAR");
	}

	@Test
	void testBackupDatabase() throws Exception {
		// Arrange
		String username = "root";
		String password = "123456";
		String database = "test_db";
		String tableName = "test_table";
		String backupFileName = "backup_test_table.sql";

		// Mock JDBC behavior
		when(mockConnection.createStatement()).thenReturn(mockStatement);
		when(mockStatement.executeQuery("SHOW CREATE TABLE " + tableName)).thenReturn(mockResultSet);
		when(mockStatement.executeQuery("SELECT * FROM " + tableName)).thenReturn(mockResultSet);

		// Mock ResultSet for "SHOW CREATE TABLE"
		when(mockResultSet.next()).thenReturn(true, false);
		when(mockResultSet.getString(2)).thenReturn("CREATE TABLE test_table (id INT, name VARCHAR(255))");

		// Mock ResultSet for "SELECT * FROM table"
		when(mockResultSet.getObject(1)).thenReturn(1);
		when(mockResultSet.getObject(2)).thenReturn("Sample");

		// Mock FileWriter
		BufferedWriter mockWriter = mock(BufferedWriter.class);
		FileWriter fileWriter = mock(FileWriter.class);

		doNothing().when(mockWriter).write(anyString());
		doNothing().when(mockWriter).close();

		// Act
		try (var writer = mockWriter) {
			backupService.backupDatabase(username, password, database, tableName, backupFileName);
		}

		// Assert
		verify(mockStatement, times(1)).executeQuery("SHOW CREATE TABLE " + tableName);
		verify(mockWriter, atLeastOnce()).write(anyString());
	}

	@Test
	void testBackupDatabaseAuto_Success() throws Exception {
		// Arrange
		String username = "root";
		String password = "123456";
		String database = "test_db";
		String tableName = "test_table";
		String backupFileName = "backup_test_table_auto.sql";

		when(mockConnection.createStatement()).thenReturn(mockStatement);
		when(mockStatement.executeQuery("SHOW CREATE TABLE " + tableName)).thenReturn(mockResultSet);
		when(mockResultSet.next()).thenReturn(true, false);
		when(mockResultSet.getString(2)).thenReturn("CREATE TABLE test_table (id INT)");

		// Act
		backupService.backupDatabaseAuto(username, password, database, tableName, backupFileName);

		// Assert
		verify(mockStatement, times(1)).executeQuery("SHOW CREATE TABLE " + tableName);
	}

	@Test
	void testScheduleAutoBackup() {
		// Arrange
		String username = "root";
		String password = "123456";
		String database = "test_db";
		String[] tableNames = { "table1", "table2" };

		// Act
		assertDoesNotThrow(() -> backupService.scheduleAutoBackup(username, password, database, tableNames));
	}
}
