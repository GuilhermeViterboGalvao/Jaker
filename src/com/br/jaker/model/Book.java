package com.br.jaker.model;

import java.util.Date;
import java.util.List;

public class Book {

	private String title;
	
	private List<Author> authors;
	
	private Date date;
	
	private List<String> contents;
	
	private JakerOptions jakerOptions;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public List<Author> getAuthors() {
		return authors;
	}

	public void setAuthors(List<Author> authors) {
		this.authors = authors;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public List<String> getContents() {
		return contents;
	}

	public void setContents(List<String> contents) {
		this.contents = contents;
	}

	public JakerOptions getJakerOptions() {
		return jakerOptions;
	}

	public void setJakerOptions(JakerOptions jakerOptions) {
		this.jakerOptions = jakerOptions;
	}
}
