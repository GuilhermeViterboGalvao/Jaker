package com.br.jaker.model;

import java.io.Serializable;

/**
 * @author guilherme
 * @email catanduva.gvg@gmail.com
 * */
public class Address implements Serializable {

	private static final long serialVersionUID = -6870701748391502047L;

	private String country;
	
	private String state;
	
	private String city; 
	
	private String neighborhood;
	
	private String street;
	
	private String code; 
	
	private String others;

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getNeighborhood() {
		return neighborhood;
	}

	public void setNeighborhood(String neighborhood) {
		this.neighborhood = neighborhood;
	}

	public String getStreet() {
		return street;
	}

	public void setStreet(String street) {
		this.street = street;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getOthers() {
		return others;
	}

	public void setOthers(String others) {
		this.others = others;
	}
}
