package com.trnqngmnh.library;

import java.io.IOException;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class BookProcessor {

	public JsonNode processSearchResults(String jsonResponse) {
		ObjectMapper objectMapper = new ObjectMapper();
		JsonNode rootNode = null;
		try {
			rootNode = objectMapper.readTree(jsonResponse);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return rootNode != null ? rootNode.path("docs") : null;
	}
}
