package com.br.jaker.model;

import java.io.Serializable;

public class Manager implements Serializable {

	private static final long serialVersionUID = -4497912557645356816L;

	private String name;
	
	private String email;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
}
