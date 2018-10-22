package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component


public class Alien {
	
	private int aid;
	private String aname;
	private String tech;
	
	@Autowired
	private Laptop laptop;
	
	
	public Alien() {
		super();
		System.out.println("Object is created");
	}
	public int getAid() {
		return aid;
	}
	public void setAid(int aid) {
		this.aid = aid;
	}
	public String getAname() {
		return aname;
	}
	public void setAname(String aname) {
		this.aname = aname;
	}
	public String getTech() {
		return tech;
	}
	public void setTech(String tech) {
		this.tech = tech;
	}
	

	public void show()
	{
		System.out.println("I am in spring");
		laptop.compile();
	}

}
