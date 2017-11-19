package com.comp3111.chatbot;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

public class CourseInfo {
	private static String ERROR_NOT_FOUND_COURSE = "";
	private static String ERROR_NOT_FOUND_OPTION = "ERROR: no such option";
	private static int MAX_CHAR = 300;

	private String input;
	private OPTIONS options;

	public enum OPTIONS {
		OVERVIEW, QUOTA, SCHEDULE
	}

	public CourseInfo() {
		this.input = "";
		this.options = null;
	}

	public CourseInfo(String text, OPTIONS options) {
		this.input = text;
		this.options = options;
	}

	private String OverviewPrinter(JSONObject course) {
		String output = "";
		String dc = course.getJSONObject("details").get("description").toString();
		String trim_dc = (dc.length() < MAX_CHAR) ? dc.substring(0, dc.length()) : dc.substring(0, MAX_CHAR) + "...";
		output = course.get("id").toString() + " - " + course.get("name").toString() + "\n\nDescription:\n" + trim_dc
				+ "\n\nCredit: " + course.get("credit").toString() + "\n";
		String[] detail_list = { "Attributes", "Vector", "Pre-Requisite", "Previous Code", "Exclusion" };
		for (String detail_item : detail_list) {
			String s_item = detail_item.toLowerCase();
			if (course.getJSONObject("details").has(s_item)) {
				output += detail_item + ": " + course.getJSONObject("details").get(s_item).toString() + "\n";
			}
		}
		return output;
	}

	private String QuotaPrinter(JSONObject course) {
		String output = "";
		JSONArray timetable = course.getJSONArray("sections");
		output = course.get("id").toString() + " - " + course.get("name").toString() + "\n";
		for (int n = 0; n < timetable.length(); n++) {
			JSONObject schedule_item = timetable.getJSONObject(n);
			String name = schedule_item.get("name").toString();
			String wait = schedule_item.get("wait").toString();
			String quota = schedule_item.get("quota").toString();
			String avail = schedule_item.get("avail").toString();
			String enrol = schedule_item.get("enrol").toString();
			output += "\nSection " + name + "\n" + "Quota\tEnrolled\tAvailable\tWaiting\n";
			String quota_data = String.format("%-6s%-9s%-10s%-7s", quota, enrol, avail, wait);
			output += quota_data;
		}
		return output;
	}

	private String SchedulePrinter(JSONObject course) {
		String output = "";
		JSONArray timetable_sch = course.getJSONArray("sections");
		output = course.get("id").toString() + " - " + course.get("name").toString() + "\n";
		for (int n = 0; n < timetable_sch.length(); n++) {
			JSONObject schedule_item = timetable_sch.getJSONObject(n);
			String name = schedule_item.get("name").toString();
			String instrcu = schedule_item.get("instructors").toString();
			output += "\n\nSection " + name + "\nInstructors: " + instrcu + "\n";
			JSONArray times = schedule_item.getJSONArray("classes");
			for (int m = 0; m < times.length(); m++) {
				JSONObject class_item = times.getJSONObject(m);
				String datetime = class_item.get("datetime").toString();
				String location = class_item.get("location").toString();
				if (times.length() > 1) {
					output += "\nClass " + String.valueOf(m + 1) + "\n";
				}
				output += "Time: " + datetime + "\nLocation: " + location;
			}
		}
		return output;
	}

	public String courseSearch() throws JSONException, MalformedURLException, IOException {
		URL data_url = new URL("http://api.patrickwu.cf/courses_dict.json");
		InputStreamReader in = new InputStreamReader(data_url.openStream());

		JSONTokener tokener = new JSONTokener(in);
		JSONObject obj = new JSONObject(tokener);
		JSONObject course = ((JSONObject) obj).getJSONObject("courses").getJSONObject(this.input);
		String output = "";
		switch (options) {
		case OVERVIEW:
			output = OverviewPrinter(course);
			break;
		case QUOTA:
			output = QuotaPrinter(course);
			break;
		case SCHEDULE:
			output = SchedulePrinter(course);
			break;
		default:
			output = ERROR_NOT_FOUND_OPTION;
			break;
		}
		if (output == "") {
			//doing not_found_course action
			output = ERROR_NOT_FOUND_COURSE;
		}

		return output;
	}
}
