package com.comp3111.chatbot;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

@RestController
public class GreetingController {
  @RequestMapping("/greeting")
  public String greeting () {
    return "Hello";
  }  
}
