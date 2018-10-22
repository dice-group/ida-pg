package com.mkyong.common;


public class Developer {
	private Language language;

	public Developer(Language language) {
		this.language = language;
	}


	public String toString() {
		return "Developer [language=" + language + "]";
	}

}
