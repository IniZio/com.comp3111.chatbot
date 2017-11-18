package com.comp3111.chatbot;

import java.net.*;
import java.io.*;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import lombok.extern.slf4j.Slf4j;

/**
 * Used for searching a HKSUT staff or student. Only contains one method called SearchPeople(String).
 * @author hlmaab
 *
 */
@Slf4j
public class URLConnectionReader {
	/**
	 * Searches a HKSUT staff or student on "http://www.ust.hk/search/{@param}/?sopt=people".
	 * @param args A string containing the name or ITSC of a HKSUT staff or student.
	 * @return A list of matched staffs or students.
	 * @throws Exception if cannot connect the link.
	 */
    public PeopleList SearchPeople(String args) throws Exception {
	    	String s1=args.replace(" ", "%20");
    		Document doc = Jsoup.connect("http://www.ust.hk/search/"+s1+"/?sopt=people").get();
    		//Document doc = Jsoup.connect("http://www.ust.hk/search/a%20b%20c/?sopt=people").get();
    		if (doc==null)
    			throw new Exception();
    		Element content = doc.getElementById("p-s-table");
    	    Elements inputElements = content.getElementsByTag("td"); 

    	    String [] values = new String [42];
    	    PeopleList l = new PeopleList();
    	    
    	    int i=0;
    	    
    	    for(Element td : inputElements) {
    	    		if (i<42) {
    	    	        values[i] = td.text();  
    	    	        if (values[i].equals("No result found matching your search criteria."))
    	    	        		return l;
    	    	        //log.info("values {} = {}", i, values[i]);
    	    	        i++; 			
    	    		}
    	    		else
    	    		{
    	    			PeopleList.too_many=true;
    	    		 	log.info("\nToo many results");
    	    		 	break;
    	    		}
    		}
    	    
    	    int j =0;
    	    while(j<i) {
    	    	log.info("Values {} = {}",j,  values[j]);
    	    	log.info("Values {} = {}",j+1,  values[j+1]);
    	    	log.info("Values {} = {}",j+2,  values[j+2]);
    	    	log.info("Values {} = {}",j+3,  values[j+3]);
    	    	log.info("Values {} = {}",j+4,  values[j+4]);
    	    	log.info("Values {} = {}",j+5,  values[j+5]);
    	    		
    	    	l.addPeople(values[j], values[j+1], values[j+2], values[+3], 
    	    				values[j+4], values[j+5]);
    	    		
    	    		j=j+6;
    	    }
    	    
    	    return l;
    	    	
    }

}