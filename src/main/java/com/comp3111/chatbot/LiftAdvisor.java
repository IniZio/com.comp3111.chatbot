package com.comp3111.chatbot;

import org.apache.commons.io.IOUtils;

import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * The LiftAdvisor Class is used to call API from path advisor web and formulates result as a string for reply.
 *
 * @author ApplewoodL
 */
public class LiftAdvisor {
    private String queryRoom;


    /**
     * Constructor and process user input to instance queryRoom.
     *
     * @param text A String which contains the original text from user
     */
    public LiftAdvisor(String text){
        this.queryRoom = text.trim();
        String[] textBreakdown = null;
        if (text.contains("room") || text.contains("rm")) {
            textBreakdown = text.split("\\D+");
            for (String numberFromText : textBreakdown) {
                if (!numberFromText.equals("")) {
                    this.queryRoom = numberFromText;
                    break;
                }
            }
        }
    }

    /**
     * Uses external API from path advisor to collect related results and generate list of records.
     *
     * @return A list of String of separated related results
     * @throws Exception
     */
    private List<String> getSuggestedResults() throws Exception{
        URL url = new URL("http://pathadvisor.ust.hk/phplib/search.php?keyword="+ queryRoom +"&floor=Overall&type=lift&same_floor=yes");
        String queryReturnedString = IOUtils.toString(url, Charset.defaultCharset());
        List<String> suggestedResults = new ArrayList<>();
        for (String suggestedResult:queryReturnedString.split("\\n")) {
            if (!suggestedResult.equals("") && suggestedResult.length() > 5) {
                suggestedResults.add(suggestedResult);
            }
        }
        return suggestedResults;
    }

    /**
     * Formulate list from getSuggestedResults() to output String for reply.
     *
     * @return A String of formulated related results from original source
     * @throws Exception
     */
    public String getReplyMessage() throws Exception{
        String replyMessage = "Room not found.";
        StringBuilder results = new StringBuilder();
        if (getSuggestedResults().isEmpty()){
            return replyMessage;
        }
        results.append("Here are the related results:\n\n");
        for (String suggestedResult:getSuggestedResults()){
            String[] suggested = suggestedResult.split(";");

            String[] buildingFind = suggested[suggested.length-2].split("\\d");
            String[] liftFind = suggested[0].split("\\D+");

            String building = (buildingFind.length == 0)?"Academic Building":buildingFind[0];
            if (building.equals("NAB"))
                building = "LSK";
            String liftNumber = "0";
            for (String lift:liftFind){
                if(!lift.equals("")){
                    liftNumber = lift;
                    break;
                }
            }
            String roomNumber = suggested[suggested.length-1];
            results.append("Room: " + roomNumber + ", Building: " + building + ", Lift Number: " + liftNumber + "\n");
        }
        replyMessage = results.toString();
        return replyMessage;
    }
}
