package com.comp3111.chatbot;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

public class courseInfoController {
	private static String ERROR_NOT_FOUND_COURSE = "Sorry, the course is not found. Do you mean....";
	private static String ERROR_NOT_FOUND_OPTION = "ERROR: no such option";
	private static int MAX_CHAR = 300;

	static String courseSearch(String text, String options) throws JSONException, MalformedURLException, IOException {
		try {
			URL data_url = new URL("http://api.patrickwu.cf/courses_dict.json");
			InputStreamReader in = new InputStreamReader(data_url.openStream());

			JSONTokener tokener = new JSONTokener(in);
			JSONObject obj = new JSONObject(tokener);
			JSONObject course = ((JSONObject) obj).getJSONObject("courses").getJSONObject(text);
			String output = "";
			switch (options) {
			case "ov":
				String dc = course.getJSONObject("details").get("description").toString();
				String trim_dc = (dc.length() < MAX_CHAR) ? dc.substring(0, dc.length())
						: dc.substring(0, MAX_CHAR) + "...";
				output = course.get("id").toString() + " - " + course.get("name").toString() + "\n\nDescription:\n"
						+ trim_dc + "\n\nCredit: " + course.get("credit").toString() + "\n";
				if (course.getJSONObject("details").has("attributes")) {
					JSONArray attr = course.getJSONObject("details").getJSONArray("attributes");
					output += "Common Core Type: ";
					output = jsonArrayStringAdder(output, attr, false);
				}
				if (course.getJSONObject("details").has("vector")) {
					output = "Vector: " + course.getJSONObject("details").get("vector").toString();
				}
				if (course.getJSONObject("details").has("pre-requisite")) {
					JSONArray pr = course.getJSONObject("details").getJSONArray("pre-requisite");
					output += "Pre-Requisite: ";
					output = jsonArrayStringAdder(output, pr, true);
				}
				if (course.getJSONObject("details").has("co-requisite")) {
					JSONArray cr = course.getJSONObject("details").getJSONArray("co-requisite");
					output += "Co-Requisite: ";
					output = jsonArrayStringAdder(output, cr, true);
				}
				if (course.getJSONObject("details").has("previous code")) {
					JSONArray pc = course.getJSONObject("details").getJSONArray("previous code");
					output += "Previous Code: ";
					output = jsonArrayStringAdder(output, pc, true);
				}
				if (course.getJSONObject("details").has("exclusion")) {
					JSONArray ex = course.getJSONObject("details").getJSONArray("exclusion");
					output += "Exclusion: ";
					output = jsonArrayStringAdder(output, ex, true);
				}
				break;
			case "qt":
				break;
			case "sch":
				break;
			default:
				output = ERROR_NOT_FOUND_OPTION;
				break;
			}
			if (output == "") {
				//doing not_found_course action
				output = ERROR_NOT_FOUND_COURSE + "Status: Normal";
			}

			return output;
		} catch (JSONException e) {
			//return ERROR_NOT_FOUND_COURSE;
			return "ERROR occured:" + e;
		} catch (Exception e) {
			return "ERROR occured:" + e;
		}
	}

	private static String jsonArrayStringAdder(String input, JSONArray jArr, boolean IsKey) {
		for (int i = 0; i < jArr.length(); i++) {
			if (!IsKey) {
				input += jArr.get(i);
			} else {
				JSONObject innerObj = jArr.getJSONObject(i);
				for (Iterator it = innerObj.keys(); it.hasNext();) {
					String key = (String) it.next();
					input += innerObj.get(key);
				}
			}
			if (i != jArr.length() - 1) {
				input += ", ";
			}
		}
		input += "\n";
		return input;
	}
}
