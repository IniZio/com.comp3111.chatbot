package com.comp3111.chatbot;

import java.util.ArrayList;
import java.util.List;

import lombok.extern.slf4j.Slf4j;


@Slf4j
public class PeopleList {
	private ArrayList <people> people;
	
	public PeopleList () {
		this.people=null;
	}
	
	public void addPeople (String p0, String p1, String p2, String p3, String p4, String p5) {
		people p = new people(p0, p1, p2, p3, p4, p5);
		if (this.people==null) {
			this.people=new ArrayList<people>();	
		}		
		this.people.add(p);
	}
	
	public ArrayList<people> getList(){
		return this.people;
	}

}