package com.comp3111.chatbot;

import java.net.*;
import java.io.*;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import lombok.extern.slf4j.Slf4j;


@Slf4j
public class URLConnectionReader {
    public PeopleList SearchPeople(String args) throws Exception {
	    	String s1=args.replace(" ", "%20");
    		Document doc = Jsoup.connect("http://www.ust.hk/search/"+s1+"/?sopt=people").get();
    		//Document doc = Jsoup.connect("http://www.ust.hk/search/a%20b%20c/?sopt=people").get();
    		Element content = doc.getElementById("p-s-table");
    	    Elements inputElements = content.getElementsByTag("td"); 

    	    String [] values = new String [100];
    	    int i=0;
    	    
    	    for(Element td : inputElements) {
    	        values[i] = td.text();  
    	        //log.info("values {} = {}", i, values[i]);
    	        i++; 			
    		}
    	    
    	    PeopleList l = new PeopleList();
    	    
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