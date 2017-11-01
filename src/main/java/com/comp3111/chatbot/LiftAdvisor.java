package com.comp3111.chatbot;

import org.apache.commons.io.IOUtils;

import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class LiftAdvisor {
    private String queryRoomNumber;

    public LiftAdvisor(String text){
        String[] textBreakdown = text.split("\\D+");
        for (String element:textBreakdown){
            if (!element.equals("")) {
                this.queryRoomNumber = element;
                break;
            }
        }
    }

    private List<String> getSuggestedResults() throws Exception{
        URL url = new URL("http://pathadvisor.ust.hk/phplib/search.php?keyword="+ queryRoomNumber +"&floor=Overall&type=lift&same_floor=yes");
        String queryReturnedString = IOUtils.toString(url, Charset.defaultCharset());
        List<String> suggestedResults = new ArrayList<>();
        for (String element:queryReturnedString.split("\\n")) {
            if (!element.equals("") && element.length() > 5) {
                suggestedResults.add(element);
            }
        }
        return suggestedResults;
    }

    public String getReplyMessage() throws Exception{
        String replyMessage = "Room not found.";
        StringBuilder results = new StringBuilder();
        if (getSuggestedResults().isEmpty()){
            return replyMessage;
        }
        results.append("Here are the related results:\n\n");
        for (String element:getSuggestedResults()){
            String[] suggested = element.split(";");

            String[] buildingFind = suggested[suggested.length-2].split("\\d");
            String[] liftFind = suggested[suggested.length-2].split("\\D+");

            String building = (buildingFind.length == 0)?"Academic Building":buildingFind[0];
            String liftNumber = "0";
            for (String elements:liftFind){
                if(!element.equals("")){
                    liftNumber = element;
                    break;
                }
            }
            String roomNumber = suggested[suggested.length-1];
            results.append("Room: " + roomNumber + " Building: " + building + " Lift Number: " +liftNumber + "\n");
        }
        replyMessage = results.toString();
        return replyMessage;
    }
}
