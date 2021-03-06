package com.popularmovies.classes;

/**
 * The class used to store data about a movie review; the review's author and its content (text).
 */
public class Review {
	private String author;
	private String content;

	public Review() {

	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		Review review = (Review) o;

		if (author != null ? !author.equals(review.author) : review.author != null) return false;
		return content != null ? content.equals(review.content) : review.content == null;
	}

	@Override
	public int hashCode() {
		int result = author != null ? author.hashCode() : 0;
		result = 31 * result + (content != null ? content.hashCode() : 0);
		return result;
	}

	public Review(String author, String content) {
		this.author = author;
		this.content = content;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	@Override
	public String toString() {
		return author + ": \"" + content + "\"";
	}
}
