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


public class BusETARequestHandler {
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

    public List<String> getArriveTime() throws Exception {
        URL url = new URL("http://etav3.kmb.hk/?action=geteta&lang=en&route=" + route + "&bound=" + bound + "&stop_seq=" + stop);
//        BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream(), "UTF-8"));
//        StringBuilder sb = new StringBuilder();
//        String line;
//        while ((line = br.readLine()) != null) {
//            sb.append(line);
//        }

//        JSONObject arrival = new JSONObject(sb.toString());
        String jsonString = IOUtils.toString(url, Charset.defaultCharset());
        JSONObject arrival = new JSONObject(jsonString);
        JSONArray nextBus = arrival.getJSONArray("response");
        List<String> arrivalTimes = new ArrayList<>() ;
        for (int i = 0; i < nextBus.length(); i++) {
            try{
                JSONObject item = nextBus.getJSONObject(i);
                String timeString = item.getString("t");
                String[] formattedTimeString = timeString.split("\\s+");
                arrivalTimes.add(formattedTimeString[0]);
            } catch (JSONException e) {
                arrivalTimes.add("error");
            }
        }
        return arrivalTimes;
    }

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
