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
    		//Document doc = Jsoup.connect("http://www.ust.hk/search/"+args+"/?sopt=people").userAgent("Mozilla").get();
    		Document doc = Jsoup.connect("http://www.ust.hk/search/Ma%20Hoi%20Lam/?sopt=people").get();
    		Element content = doc.getElementById("p-s-table");
    	    Elements inputElements = content.getElementsByTag("td"); 


    	    String [] values = new String [100];
    	    int i=0;
    	    
    	    for(Element td : inputElements) {
    	        values[i] = td.text();  
    	        log.info("values {} = {}", i, values[i]);
    	        i++; 			
    		}
    	    
    	    PeopleList l = new PeopleList();
    	    
    	    for (int j =0;j<i;j++) {
    	    		l.addPeople(values[j], values[j+1], values[j+2], values[+3], 
    	    				values[j+4], values[j+5]);
    	    		log.info("added people & size = {}", l.getList().size());
    	    		j=j+6;
    	    }
    	    
    	   
    	    
    	    return l;
    	    	
    }

}