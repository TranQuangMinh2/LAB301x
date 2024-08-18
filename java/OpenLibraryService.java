package com.trnqngmnh.library;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class OpenLibraryService {

	private final String OPEN_LIBRARY_SEARCH_API_URL = "https://openlibrary.org/search.json";

	public String searchBooksByTitle(String title) {
		String url = UriComponentsBuilder.fromHttpUrl(OPEN_LIBRARY_SEARCH_API_URL).queryParam("title", title)
				.toUriString();

		RestTemplate restTemplate = new RestTemplate();
		return restTemplate.getForObject(url, String.class);
	}

	public String searchBooksByAuthor(String author) {
		String url = UriComponentsBuilder.fromHttpUrl(OPEN_LIBRARY_SEARCH_API_URL).queryParam("author", author)
				.toUriString();

		RestTemplate restTemplate = new RestTemplate();
		return restTemplate.getForObject(url, String.class);
	}

	public String searchBooksByTitleWithPage(String title, int page) {
		String url = UriComponentsBuilder.fromHttpUrl(OPEN_LIBRARY_SEARCH_API_URL).queryParam("q", title)
				.queryParam("page", page).toUriString();

		RestTemplate restTemplate = new RestTemplate();
		return restTemplate.getForObject(url, String.class);
	}
}
