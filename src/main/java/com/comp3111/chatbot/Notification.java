package com.comp3111.chatbot;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.linecorp.bot.client.LineMessagingClient;
import com.linecorp.bot.client.MessageContentResponse;
import com.linecorp.bot.model.Multicast;
import com.linecorp.bot.model.PushMessage;
import com.linecorp.bot.model.ReplyMessage;
import com.linecorp.bot.model.message.TextMessage;
import com.linecorp.bot.model.profile.MembersIdsResponse;
import com.linecorp.bot.model.profile.UserProfileResponse;
import com.linecorp.bot.model.response.BotApiResponse;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class Notification {
  private static SQLDatabaseEngine db = new SQLDatabaseEngine();
  @Autowired
  private LineMessagingClient lineMessagingClient ;
  // Everyday 9am
  @Scheduled(cron="0 9 0 * * ?")
  //@Scheduled(cron="*/5 * * * * *")    
  public void refreshNotifications () {
    log.info("Refreshing notifications");
    String[] subscriberIds = new String[0];
    try { subscriberIds = db.getSubscriberIDs(); } catch (Exception e) {
      log.info("Failed to get subscribers : {}", e.toString());      
    }
    Set<String> subscriberSet = new HashSet<String>(Arrays.asList(subscriberIds));
    for (String subscriber: subscriberIds) {
      // Todos notification
      try {
        String reply = "Below are your existing Todos:\n";
        int index = 0;
        try {
          for (Todo item: db.getTodos(subscriber)) {
              reply += "" + (++index) + ") " + item.getContent() + " by " + new SimpleDateFormat("dd/MM/yyyy").format(item.getDeadline()) + "\n";
          }
        }  catch(Exception e) {
          log.info("Failed to get todos for notification : {}", e.toString());            
        }
        PushMessage pushMessage = new PushMessage(subscriber, new TextMessage(reply));
        lineMessagingClient.pushMessage(pushMessage);
      } catch (Exception e) {
        log.info("Failed to push message: {}", e.toString());
      }
    }
  }
}
