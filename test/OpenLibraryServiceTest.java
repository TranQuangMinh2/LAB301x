package com.trnqngmnh.library;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.client.RestTemplate;

import com.trnqngmnh.library.service.OpenLibraryService;

class OpenLibraryServiceTest {

	@Mock
	private RestTemplate restTemplate;

	@InjectMocks
	private OpenLibraryService openLibraryService;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	void testSearchBooksByTitle_Success() {
		// Arrange
		String title = "Java Programming";
		String mockResponse = "{\"docs\":[],\"numFound\":0,\"start\":0}";
		String expectedUrl = "https://openlibrary.org/search.json?title=Java%20Programming";

		when(restTemplate.getForObject(expectedUrl, String.class)).thenReturn(mockResponse);

		// Act
		String result = openLibraryService.searchBooksByTitle(title);

		// Assert
		assertEquals(mockResponse, result);
		verify(restTemplate, times(1)).getForObject(expectedUrl, String.class);
	}

	@Test
	void testSearchBooksByAuthor_Success() {
		// Arrange
		String author = "John Doe";
		String mockResponse = "{\"docs\":[],\"numFound\":0,\"start\":0}";
		String expectedUrl = "https://openlibrary.org/search.json?author=John%20Doe";

		when(restTemplate.getForObject(expectedUrl, String.class)).thenReturn(mockResponse);

		// Act
		String result = openLibraryService.searchBooksByAuthor(author);

		// Assert
		assertEquals(mockResponse, result);
		verify(restTemplate, times(1)).getForObject(expectedUrl, String.class);
	}

	@Test
	void testSearchBooksByTitleWithPage_Success() {
		// Arrange
		String title = "Spring Boot";
		int page = 2;
		String mockResponse = "{\"docs\":[],\"numFound\":0,\"start\":0}";
		String expectedUrl = "https://openlibrary.org/search.json?q=Spring%20Boot&page=2";

		when(restTemplate.getForObject(expectedUrl, String.class)).thenReturn(mockResponse);

		// Act
		String result = openLibraryService.searchBooksByTitleWithPage(title, page);

		// Assert
		assertEquals(mockResponse, result);
		verify(restTemplate, times(1)).getForObject(expectedUrl, String.class);
	}

	@Test
	void testSearchBooksByTitle_Failure() {
		// Arrange
		String title = "Nonexistent";
		String expectedUrl = "https://openlibrary.org/search.json?title=Nonexistent";

		when(restTemplate.getForObject(expectedUrl, String.class)).thenThrow(new RuntimeException("API Error"));

		// Act & Assert
		Exception exception = assertThrows(RuntimeException.class, () -> openLibraryService.searchBooksByTitle(title));
		assertEquals("API Error", exception.getMessage());
		verify(restTemplate, times(1)).getForObject(expectedUrl, String.class);
	}
}
