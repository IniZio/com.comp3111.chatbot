package com.comp3111.chatbot;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;
import org.json.JSONTokener;
import org.json.JSONWriter;


public class courseInfoController {
	private static String ERROR_NOT_FOUND_COURSE = "Sorry, the course is not found. Do you mean....";
	private static String ERROR_NOT_FOUND_OPTION = "ERROR: no such option";

	static String courseSearch(String text, String options)throws JSONException, MalformedURLException, IOException{
		try{
		URL data_url = new URL("http://api.patrickwu.cf/courses_dict.json");
		InputStreamReader in = new InputStreamReader(data_url.openStream());
		
		JSONTokener tokener = new JSONTokener(in);
		JSONObject obj = new JSONObject(tokener);
		JSONObject course = ((JSONObject)obj).getJSONObject("courses").getJSONObject(text);
		String output = ""; 
		switch(options){
			case "ov":
			output = course.get("id").toString()+" - "+ course.get("name").toString()+"\n\nDescription:\n"+course.getJSONObject("details").get("description").toString()+"\n\nCredit: "+course.get("credit").toString();
			break;
			case "qt":
			break;
			case "sch":
			break;
			default:
			output = ERROR_NOT_FOUND_OPTION;
			break;
		}
		if(output == "")
		{
			//doing not_found_course action
			output = ERROR_NOT_FOUND_COURSE+"Status: Normal";
		}
		
		return output;}
		catch(JSONException e){
			return ERROR_NOT_FOUND_COURSE;
		}
		catch(Exception e){
			return "ERROR occured. PLase contact the team with rpoviding following data:"+e;
		}
	}
}
