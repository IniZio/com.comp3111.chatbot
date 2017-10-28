package com.comp3111.chatbot;

import lombok.extern.slf4j.Slf4j;


@Slf4j
public class people{
	private String name;
	private String email;
	private String phone;
	private String department;
	private String room;
	
	public people( String name,  String email,  String phone,
			 String department,  String room){
		this.name=name;
		this.name=email;
		this.name=phone;
		this.name=department;
		this.name=room;
		
	}
	
	public String getName() {
		return this.name;
	}
	public String getEmail() {
		return this.email;
	}
	public String getPhone() {
		return this.phone;
	}
	public String getDepartment() {
		return this.department;
	}
	public String getRoom() {
		return this.room;
	}
}