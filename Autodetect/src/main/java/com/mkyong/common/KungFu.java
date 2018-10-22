package com.mkyong.common;

import org.springframework.stereotype.Component;

@Component("kungfu")

public class KungFu {
	private String name;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return "Language [name=" + name + "]";
	}

}