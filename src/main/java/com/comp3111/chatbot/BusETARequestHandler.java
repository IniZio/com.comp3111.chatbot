package com.comp3111.chatbot;

import org.json.JSONObject;
import org.json.JSONArray;
import org.json.JSONException;

//import java.io.InputStreamReader;
import java.net.URL;
import java.lang.String;
//import java.io.BufferedReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.io.IOUtils;

/**
 * The BusETARequestHandler Class is used to call API obtained from reverse engineering of KMB Android app and formulates result as a string for reply.
 *
 * @author ApplewoodL
 */
public class BusETARequestHandler {

    /**
     * Constructor which takes in route number and direction for API.
     *
     * @param route The bus route number
     * @param bound The direction in terms of the standard given from KMB API
     */
    public BusETARequestHandler (String route, String bound){
        this.route = route;
        this.bound = bound;
        if (route.equals("91M") && bound.equals("1")){
            this.stop = "12";
        }
        else if (route.equals("91M") && bound.equals("2")){
            this.stop = "16";
        }
        else
            this.stop = "15";
    }
    private String route;
    private String bound;
    private  String stop;

    /**
     * Uses external API from KMB app to collect JSON string and decode the JSON to get the list of arrival times of bus.
     *
     * @return A list of String of at most 3 arrival times or error if API return nothing
     * @throws Exception
     */
    public List<String> getArriveTime() throws Exception {
        URL url = new URL("http://etav3.kmb.hk/?action=geteta&lang=en&route=" + route + "&bound=" + bound + "&stop_seq=" + stop);
        String jsonString = IOUtils.toString(url, Charset.defaultCharset());
        JSONObject arrival = new JSONObject(jsonString);
        JSONArray nextBus = arrival.getJSONArray("response");
        List<String> arrivalTimes = new ArrayList<>() ;
        for (int i = 0; i < nextBus.length(); i++) {
            try{
                JSONObject item = nextBus.getJSONObject(i);
                String timeString = item.getString("t");
                String[] formattedTimeString = timeString.split("[^\\w+:]");
                if (!formattedTimeString[0].matches("\\d{2}:\\d{2}")){
                    arrivalTimes.add(timeString);
                    break;
                }
                arrivalTimes.add(formattedTimeString[0].trim());
            } catch (JSONException e) {
                arrivalTimes.add("error");
            }
        }
        return arrivalTimes;
    }

    /**
     * Formulate list from getArriveTime() to output String for reply.
     *
     * @return A String of formulated results from original source
     * @throws Exception
     */
    public String getReplyMessage() throws Exception{
        String replyMessage = "no info";
        StringBuilder results = new StringBuilder();
        for (String element:getArriveTime()){
            if (element.equals("error")){
                replyMessage = "currently no info";
                break;
            }
            results.append(element);
            results.append(" ");
            replyMessage = results.toString();
        }
        return replyMessage;
    }
}
