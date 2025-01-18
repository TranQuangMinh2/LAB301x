package com.trnqngmnh.library;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.BufferedReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.client.RestTemplate;

import com.trnqngmnh.library.config.CaptchaConfig;
import com.trnqngmnh.library.service.CaptchaService;

class CaptchaServiceTest {

	@InjectMocks
	private CaptchaService captchaService;

	@Mock
	private CaptchaConfig captchaConfig;

	@Mock
	private RestTemplate restTemplate;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	void testVerifyRecaptcha_Success() throws Exception {
		String mockResponse = "{\"success\": true}";

		// Mocking HttpURLConnection
		HttpURLConnection connection = mock(HttpURLConnection.class);
		BufferedReader reader = mock(BufferedReader.class);

		URL url = mock(URL.class);
		when(url.openConnection()).thenReturn(connection);

		when(connection.getInputStream()).thenReturn(new java.io.ByteArrayInputStream(mockResponse.getBytes()));
		when(connection.getResponseCode()).thenReturn(200);

		// Mock response
		when(reader.readLine()).thenReturn(mockResponse, (String) null);

		boolean result = captchaService.verifyRecaptcha("mockCaptchaResponse");
		assertTrue(result);
	}

	@Test
	void testVerifyRecaptcha_Failure() throws Exception {
		String mockResponse = "{\"success\": false}";

		// Mocking HttpURLConnection
		HttpURLConnection connection = mock(HttpURLConnection.class);
		BufferedReader reader = mock(BufferedReader.class);

		URL url = mock(URL.class);
		when(url.openConnection()).thenReturn(connection);

		when(connection.getInputStream()).thenReturn(new java.io.ByteArrayInputStream(mockResponse.getBytes()));
		when(connection.getResponseCode()).thenReturn(200);

		boolean result = captchaService.verifyRecaptcha("mockCaptchaResponse");
		assertFalse(result);
	}

	@Test
	void testVerifyRecaptcha_ExceptionHandling() throws Exception {
		URL url = mock(URL.class);
		when(url.openConnection()).thenThrow(new RuntimeException("Connection error"));

		boolean result = captchaService.verifyRecaptcha("mockCaptchaResponse");
		assertFalse(result);
	}
}
