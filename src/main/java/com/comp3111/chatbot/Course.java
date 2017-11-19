package com.comp3111.chatbot;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.*;
import org.json.*;

public class Course {
    private String course_name;
    private JSONArray obj = null;

    public Course() {
        this.course_name = "";
    }

    public Course(String name) throws IOException, MalformedURLException {
        this.course_name = name;
        URL data_url = new URL("http://api.patrickwu.cf/courses_list.json");
        InputStreamReader in = new InputStreamReader(data_url.openStream());

        JSONTokener tokener = new JSONTokener(in);
        this.obj = new JSONArray(tokener);
    }

    public static String extractCourseFromText(String input){  
        final Pattern pattern = Pattern.compile("([A-Z]|[a-z]){4}\\d{4}([A-Z]|[a-z])?");
        final Matcher matcher = pattern.matcher(input);
        matcher.find();
        return matcher.group(0).toUpperCase();
    }

    public String getName() {
        return course_name;
    }

    public boolean coursePreChecker() {
        boolean ismatch = false;
        for (int i = 0; i < obj.length(); i++) {
            String course = obj.getJSONObject(i).toString();
            ismatch = (course == this.course_name);
        }
        return ismatch;
    }

    public List<String> similarCourseRecommendation() {
        List<String> result_course_list = new ArrayList<String>();
        for (int i = 0; i < obj.length(); i++) {
            String course = obj.getJSONObject(i).toString();
            WagnerFischer wf = new WagnerFischer(this.course_name, course);
            if (wf.getDistance() < 4) {
                result_course_list.add(course);
            }
        }
        return result_course_list;
    }
}