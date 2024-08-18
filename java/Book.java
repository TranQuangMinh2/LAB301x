package com.trnqngmnh.library;

public class Book {
	private String title;
	private String authors;
	private String publishDate;
	private int numberOfPages;
	private int coverId;

	// Getter và Setter cho title
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	// Getter và Setter cho authors
	public String getAuthors() {
		return authors;
	}

	public void setAuthors(String authors) {
		this.authors = authors;
	}

	// Getter và Setter cho publishDate
	public String getPublishDate() {
		return publishDate;
	}

	public void setPublishDate(String publishDate) {
		this.publishDate = publishDate;
	}

	// Getter và Setter cho numberOfPages
	public int getNumberOfPages() {
		return numberOfPages;
	}

	public void setNumberOfPages(int numberOfPages) {
		this.numberOfPages = numberOfPages;
	}

	// Getter và Setter cho coverId
	public int getCoverId() {
		return coverId;
	}

	public void setCoverId(int coverId) {
		this.coverId = coverId;
	}
}
