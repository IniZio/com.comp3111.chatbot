package com.comp3111.chatbot;

import lombok.extern.slf4j.Slf4j;

/**
 * Represents a HKUST staff or student.
 * @author hlmaab
 *
 */
@Slf4j
public class people{
	private String title;
	private String name;
	private String email;
	private String phone;
	private String department;
	private String room;
	/**
	 * Constructor.
	 * @param title
	 * @param name
	 * @param email
	 * @param phone
	 * @param department
	 * @param room
	 */
	public people( String title, String name,  String email,  String phone,
			 String department,  String room){
		this.title=title;
		this.name=name;
		this.email=email;
		this.phone=phone;
		this.department=department;
		this.room=room;
		
	}
	/** 
	* Gets the title of a staff or student.
	* @return A string representing the title of a staff or student.
	*/
	public String getTitle() {
		if (this.title.equals(""))
			return "No info";
		return this.title;
	}
	/** 
	* Gets the name of a staff or student.
	* @return A string representing the name of a staff or student.
	*/
	public String getName() {
		if (this.name.equals(""))
			return "No info";
		return this.name;
	}
	/** 
	* Gets the email address of a staff or student.
	* @return A string representing the email address of a staff or student.
	*/
	public String getEmail() {
		if (this.email.equals(""))
			return "No info";
		return this.email;
	}
	/** 
	* Gets the phone number of a staff or student.
	* @return A string representing the phone number of a staff or student.
	*/
	public String getPhone() {
		if (this.phone.equals(""))
			return "No info";
		return this.phone;
	}
	/** 
	* Gets the department of a staff or student.
	* @return A string representing the department of a staff or student.
	*/
	public String getDepartment() {
		if (this.department.equals(""))
			return "No info";
		return this.department;
	}
	/** 
	* Gets the room number of a staff.
	* @return A string representing the room number of a staff.
	*/
	public String getRoom() {
		if (this.room.equals(""))
			return "No info";
		return this.room;
	}
}