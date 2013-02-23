package com.br.jaker.model;

public class Edition {

	private boolean newEdition;
	
	private int number;
	
	private String title;
	
	private String description;
	
	private String coverImage;
	
	private String downloadUrl;

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
}