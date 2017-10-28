package com.comp3111.chatbot;

import java.net.*;
import java.io.*;

public class URLConnectionReader {
    public static void main(String args) throws Exception {
    		Document doc = Jsoup.connect("http://www.ust.hk/search/"+args+"/?sopt=people").get();
    }
}