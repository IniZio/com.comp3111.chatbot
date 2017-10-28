package com.comp3111.chatbot;

import org.json.JSONObject;
import org.json.JSONArray;
import org.json.JSONException;


import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.List;


public class BusETARequestHandler {
    public List<String> getArriveTime() throws Exception {
        URL url = new URL("http://etav3.kmb.hk/?action=geteta&lang=en&route=91M&bound=1&stop_seq=12");
        BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream(), "UTF-8"));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) {
            sb.append(line);
        }

        JSONObject arrival = new JSONObject(sb.toString());
        JSONArray nextBus = arrival.getJSONArray("response");
        List<String> arrivalTimes = new ArrayList<>() ;
        for (int i = 0; i < nextBus.length(); i++) {
            try{
                JSONObject item = nextBus.getJSONObject(i);
                arrivalTimes.add(item.getString("t"));
            } catch (JSONException e) {
                arrivalTimes.add("error");
            }
        }
        return arrivalTimes;
    }
}
