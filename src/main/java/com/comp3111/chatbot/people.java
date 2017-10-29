package com.comp3111.chatbot;

import lombok.extern.slf4j.Slf4j;


@Slf4j
public class people{
	private String title;
	private String name;
	private String email;
	private String phone;
	private String department;
	private String room;
	
	public people( String title, String name,  String email,  String phone,
			 String department,  String room){
		this.title=title;
		this.name=name;
		this.email=email;
		this.phone=phone;
		this.department=department;
		this.room=room;
		
	}
	
	public String getTitle() {
		if (this.title==null)
			return "No info";
		return this.title;
	}
	
	public String getName() {
		if (this.name==null)
			return "No info";
		return this.name;
	}
	public String getEmail() {
		if (this.email==null)
			return "No info";
		return this.email;
	}
	public String getPhone() {
		if (this.phone==null)
			return "No info";
		return this.phone;
	}
	public String getDepartment() {
		if (this.department==null)
			return "No info";
		return this.department;
	}
	public String getRoom() {
		if (this.room==null)
			return "No info";
		return this.room;
	}
}