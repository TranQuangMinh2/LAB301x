package com.trnqngmnh.library;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.BufferedReader;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import com.trnqngmnh.library.service.RestoreService;

class RestoreServiceTest {

	private RestoreService restoreService;

	private final String USERNAME = "root";
	private final String PASSWORD = "password";
	private final String DATABASE = "test_db";
	private final String BACKUP_FILENAME = "backup.sql";

	@BeforeEach
	void setUp() {
		restoreService = new RestoreService();
	}

	@Test
	void testRestoreDatabase_Success() throws Exception {
		try (MockedStatic<DriverManager> driverManagerMock = mockStatic(DriverManager.class);
				MockedStatic<Class> classMock = mockStatic(Class.class);
				MockedStatic<BufferedReader> bufferedReaderMock = mockStatic(BufferedReader.class);) {
			Connection mockConnection = mock(Connection.class);
			Statement mockStatement = mock(Statement.class);
			BufferedReader mockReader = mock(BufferedReader.class);

			// Mock DriverManager behavior
			driverManagerMock.when(() -> DriverManager.getConnection(anyString(), anyString(), anyString()))
					.thenReturn(mockConnection);

			// Mock Connection and Statement behavior
			when(mockConnection.createStatement()).thenReturn(mockStatement);

			// Mock BufferedReader behavior
			bufferedReaderMock.when(() -> new BufferedReader(any(FileReader.class))).thenReturn(mockReader);
			when(mockReader.readLine()).thenReturn("CREATE TABLE test (id INT);", "INSERT INTO test VALUES (1);", null);

			// Execute the method
			restoreService.restoreDatabase(USERNAME, PASSWORD, DATABASE, BACKUP_FILENAME);

			// Verify SQL execution
			verify(mockStatement, times(2)).execute(anyString());
			verify(mockReader, times(3)).readLine();
			verify(mockConnection).close();
			verify(mockStatement).close();
		}
	}

	@Test
	void testRestoreDatabase_FileNotFound() {
		Exception exception = assertThrows(SQLException.class, () -> {
			restoreService.restoreDatabase(USERNAME, PASSWORD, DATABASE, "nonexistent.sql");
		});

		assertTrue(exception.getMessage().contains("Error reading backup file."));
	}

	@Test
	void testRestoreDatabase_JdbcDriverNotFound() {
		try (MockedStatic<Class> classMock = mockStatic(Class.class)) {
			classMock.when(() -> Class.forName(anyString())).thenThrow(ClassNotFoundException.class);

			Exception exception = assertThrows(SQLException.class, () -> {
				restoreService.restoreDatabase(USERNAME, PASSWORD, DATABASE, BACKUP_FILENAME);
			});

			assertTrue(exception.getMessage().contains("JDBC Driver not found."));
		}
	}

	@Test
	void testRestoreDatabase_ConnectionFailure() {
		try (MockedStatic<DriverManager> driverManagerMock = mockStatic(DriverManager.class)) {
			driverManagerMock.when(() -> DriverManager.getConnection(anyString(), anyString(), anyString()))
					.thenThrow(SQLException.class);

			Exception exception = assertThrows(SQLException.class, () -> {
				restoreService.restoreDatabase(USERNAME, PASSWORD, DATABASE, BACKUP_FILENAME);
			});

			assertTrue(exception.getMessage().contains("JDBC Driver not found."));
		}
	}
}
