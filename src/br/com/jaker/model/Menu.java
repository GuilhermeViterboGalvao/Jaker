package br.com.jaker.model;

import java.io.Serializable;

public class Menu implements Serializable {

	private static final long serialVersionUID = -1691305981149410820L;

	private String position;
	
	private int height;
	
	private String[] buttonsImages;

	public String getPosition() {
		return position == null || !position.equals("") ? "top" : position;
	}

	public void setPosition(String position) {
		this.position = position;
	}

	public int getHeight() {
		return height <= 0 ? 40 : height;
	}

	public void setHeight(int height) {
		this.height = height <= 100 ? height : 40;
	}

	public String[] getButtonsImages() {
		return buttonsImages;
	}

	public void setButtonsImages(String[] buttonsImages) {
		this.buttonsImages = buttonsImages;
	}	
}