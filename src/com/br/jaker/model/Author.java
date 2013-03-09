package com.br.jaker.model;

import java.io.Serializable;

/**
 * @author guilherme
 * @email catanduva.gvg@gmail.com
 * */
public class Author implements Serializable {

	private static final long serialVersionUID = 489278591356255593L;

	private String name;
	
	private String link;
	
	private String email;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
}