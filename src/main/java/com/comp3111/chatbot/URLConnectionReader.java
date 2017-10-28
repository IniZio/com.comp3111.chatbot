package com.comp3111.chatbot;

import java.net.*;
import java.io.*;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;



public class URLConnectionReader {
    void SearchPeople(String args) throws Exception {
    		Document doc = Jsoup.connect("http://www.ust.hk/search/"+args+"/?sopt=people").get();
    		
    		Element content = doc.getElementById("p-s-table");
    	    Elements inputElements = content.getElementsByTag("td"); 


    	    for(Element td : inputElements) {
    	        String value = td.text();  
    	        //System.out.println("result: "+value);   			
    		}

    	    
    		//String [] result=new String [6];
    		//return new people (result[0], result[1], result[2], result[3], result[4], result[5]);
    }
}