package br.com.jaker.model;

import java.io.Serializable;

/**
 * @author guilherme
 * @email catanduva.gvg@gmail.com
 * */
public class Enterprise implements Serializable {
	
	private static final long serialVersionUID = 4940485209011527546L;

	private String name;
	
	private String site;
	
	private String urlJsonEdtions;
	
	private Manager manager;
	
	private Address address;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSite() {
		return site;
	}

	public void setSite(String site) {
		this.site = site;
	}

	public String getUrlJsonEdtions() {
		return urlJsonEdtions;
	}

	public void setUrlJsonEdtions(String urlJsonEdtions) {
		this.urlJsonEdtions = urlJsonEdtions;
	}

	public Manager getManager() {
		return manager;
	}

	public void setManager(Manager manager) {
		this.manager = manager;
	}

	public Address getAddress() {
		return address;
	}

	public void setAddress(Address address) {
		this.address = address;
	}	
}