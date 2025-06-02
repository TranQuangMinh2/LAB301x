package com.trnqngmnh.library.config;

import org.springframework.web.multipart.MultipartFile;

public class CSVHelper {

	@SuppressWarnings("null")
	public static boolean hasCSVFormat(MultipartFile file) {
		return "text/csv".equals(file.getContentType()) || file.getOriginalFilename().endsWith(".csv");
	}
}
