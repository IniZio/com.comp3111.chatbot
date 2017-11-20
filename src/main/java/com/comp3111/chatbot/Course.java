package com.comp3111.chatbot;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import static java.lang.Math.min;

import org.json.JSONArray;
import org.json.JSONTokener;

import lombok.extern.slf4j.Slf4j;

/**
 * Course calss which use to find the course information corresponding to the course name
 */
@Slf4j
public class Course {
    private String course_name;
    private JSONArray obj = null;

    /**
     * Empty Constructor.
     */
    public Course() {
        this.course_name = "";
    }

    /**
     * Constructor.
     * @param name A string containing the course name.
     * @exception IOException,MalformedURLException
     */
    public Course(String name) throws IOException, MalformedURLException {
        this.course_name = name;
        URL data_url = new URL("http://api.patrickwu.cf/courses_list.json");
        InputStreamReader in = new InputStreamReader(data_url.openStream());
        JSONTokener tokener = new JSONTokener(in);
        this.obj = new JSONArray(tokener);
    }

    /**
     * Static function to extract Course Code from text.
     * @param input A string containing the raw text.
     * @return A string containing course code.
     */
    public static String extractCourseFromText(String input) {
        final Pattern pattern = Pattern.compile("([A-Z]|[a-z]){4}(\\s)?\\d{4}([A-Z]|[a-z])?");
        final Matcher matcher = pattern.matcher(input);
        matcher.find();
        return matcher.group(0).toUpperCase().replaceAll("\\s+", "");
    }
    /**
     * Return the Course Name(Course Code)
     * @return A string containing course code.
     */
    public String getName() {
        return course_name;
    }

    /**
     * Check if the course code is valid.
     * @return A boolean for whether the course code is valid, true for valid, false for invalid.
     */
    public boolean coursePreChecker() {
        for (int i = 0; i < obj.length(); i++) {
            String course = obj.get(i).toString();
            if (course.matches(this.course_name)) {
                log.info("course_found" + course);
                return true;
            }
        }
        return false;
    }
    /**
     * Recommendation for similar course if course code is not found.
     * @return A list of strings containing course codes.
     */
    public List<String> similarCourseRecommendation() {
        Map<String, Integer> result_priority_list = new HashMap<>();
        int count = 0;
        for (int i = 0; i < obj.length(); i++) {
            String course = obj.get(i).toString();
            WagnerFischer wf = new WagnerFischer(this.course_name, course);
            if (wf.getDistance() < 7) {
                result_priority_list.put(course, wf.getDistance());
            }
        }
        List<String> result_course_list = result_priority_list.entrySet().stream()
                .sorted(Comparator.comparing(Map.Entry::getValue)).map(Map.Entry::getKey).collect(Collectors.toList());
        List<String> result_trim_course_list = result_course_list.subList(0, min(result_course_list.size(), 10));

        return result_trim_course_list;
    }
}