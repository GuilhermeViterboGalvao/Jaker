package br.com.jaker.model;

import java.io.Serializable;

/**
 * @author guilherme
 * @email catanduva.gvg@gmail.com
 * */
public class Edition implements Serializable{

	private static final long serialVersionUID = -1989510013903377541L;

	private boolean newEdition;
	
	private int number;
	
	private String title;
	
	private String description;
	
	private String coverImage;
	
	private String downloadUrl;
	
	private Book book;

	public boolean isNewEdition() {
		return newEdition;
	}

	public void setNewEdition(boolean newEdition) {
		this.newEdition = newEdition;
	}

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getCoverImage() {
		return coverImage;
	}

	public void setCoverImage(String coverImage) {
		this.coverImage = coverImage;
	}

	public String getDownloadUrl() {
		return downloadUrl;
	}

	public void setDownloadUrl(String downloadUrl) {
		this.downloadUrl = downloadUrl;
	}
	
	public Book getBook() {
		return book;
	}

	public void setBook(Book book) {
		this.book = book;
	}
}