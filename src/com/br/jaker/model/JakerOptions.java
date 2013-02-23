package com.br.jaker.model;

public class JakerOptions {

	private String background;
	
	private boolean verticalBounce;
	
	private int indexHeight;
	
	private String backgroundImagePortrait;
	
	private String backgroundImageLandscape;
	
	private String pageNumbersColor;

	public String getBackground() {
		return background;
	}

	public void setBackground(String background) {
		this.background = background;
	}

	public boolean isVerticalBounce() {
		return verticalBounce;
	}

	public void setVerticalBounce(boolean verticalBounce) {
		this.verticalBounce = verticalBounce;
	}

	public int getIndexHeight() {
		return indexHeight;
	}

	public void setIndexHeight(int indexHeight) {
		this.indexHeight = indexHeight;
	}

	public String getBackgroundImagePortrait() {
		return backgroundImagePortrait;
	}

	public void setBackgroundImagePortrait(String backgroundImagePortrait) {
		this.backgroundImagePortrait = backgroundImagePortrait;
	}

	public String getBackgroundImageLandscape() {
		return backgroundImageLandscape;
	}

	public void setBackgroundImageLandscape(String backgroundImageLandscape) {
		this.backgroundImageLandscape = backgroundImageLandscape;
	}

	public String getPageNumbersColor() {
		return pageNumbersColor;
	}

	public void setPageNumbersColor(String pageNumbersColor) {
		this.pageNumbersColor = pageNumbersColor;
	}
}