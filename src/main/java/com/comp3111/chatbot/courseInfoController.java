package com.comp3111.chatbot;
import java.util.List;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;
import org.json.*;

public class courseInfoController {
	
	static String courseSearch(String text){
		JSONObject obj = new JSONObject("../resources/courses_dict.json");
		
		JSONArray courses = obj.getJSONArray(text);
		
		for (Object o : courses)
		{
			JSONObject person = (JSONObject) o;

			String name = (String) person.get("name");
			System.out.println(name);

			String city = (String) person.get("city");
			System.out.println(city);

			String job = (String) person.get("job");
			System.out.println(job);

			JSONArray cars = (JSONArray) person.get("cars");

			for (Object c : cars)
			{
				System.out.println(c+"");
			}
		}
		return "test";
	}
}
