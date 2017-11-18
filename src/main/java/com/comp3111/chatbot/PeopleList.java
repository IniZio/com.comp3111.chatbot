package com.comp3111.chatbot;

import java.util.ArrayList;
import java.util.List;

import lombok.extern.slf4j.Slf4j;

/**
 * Represents a list of HKUST staffs or students.
 * @author hlmaab
 *
 */
@Slf4j
public class PeopleList {
	private ArrayList <people> people;
	public static boolean too_many=false;
	/**
	 * Constructor.
	 */
	public PeopleList () {
		this.people=null;
	}
	/** 
	* Add a staff or student to the list.
	* @param p0 A string containing the title.
	* @param p1 A string containing the name.
	* @param p2 A string containing the email address.
	* @param p3 A string containing the phone number.
	* @param p4 A string containing the department.
	* @param p5 A string containing the room number.
	*/
	public void addPeople (String p0, String p1, String p2, String p3, String p4, String p5) {
		people p = new people(p0, p1, p2, p3, p4, p5);
		if (this.people==null) {
			this.people=new ArrayList<people>();	
		}		
		this.people.add(p);
	}
	/** 
	* Gets the list of HKUST staffs or students.
	* @return A list of HKUST staffs or students .
	*/
	public ArrayList<people> getList(){
		return this.people;
	}

}