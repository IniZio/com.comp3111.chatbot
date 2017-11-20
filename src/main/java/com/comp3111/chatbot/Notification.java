package com.comp3111.chatbot;

import java.text.SimpleDateFormat;
import java.time.*;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

import java.time.ZonedDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

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
  private LineMessagingClient lineMessagingClient;

  // Everyday 9am
  //@Scheduled(cron = "0 0 9 * * *")
  @Scheduled(cron="*/30 * * * * *")
  public void refreshNotifications() {
    LocalDate current = LocalDate.now(ZoneId.of("UTC+08:00"));
    if (!((current.getYear() == 2017) && (current.getMonthValue() == 11)
        && ((current.getDayOfMonth()) >= 20 && (current.getDayOfMonth() <= 27)))) {
      return;
    }
    log.info("Refreshing notifications");
    String[] subscriberIds = new String[0];
    try {
      subscriberIds = db.getSubscriberIDs();
    } catch (Exception e) {
      log.info("Failed to get subscribers : {}", e.toString());
    }
    Set<String> subscriberSet = new HashSet<String>(Arrays.asList(subscriberIds));
    for (String subscriber : subscriberIds) {
      // Todos notification
      try {
        String reply = "Below are your existing Todos:\n";
        int index = 0;
        try {
          for (Todo item : db.getTodos(subscriber)) {
            reply += "" + (++index) + ") " + item.getContent() + " by "
                + new SimpleDateFormat("dd/MM/yyyy").format(item.getDeadline()) + "\n";
          }
        } catch (Exception e) {
          log.info("Failed to get todos for notification : {}", e.toString());
        }
        PushMessage pushMessage = new PushMessage(subscriber.trim(), new TextMessage(reply));
        lineMessagingClient.pushMessage(pushMessage);
      } catch (Exception e) {
        log.info("Failed to push message: {}", e.toString());
      }
    }
//    ZonedDateTime currentTime = ZonedDateTime.now(ZoneId.of("UTC+8"));
//    String timeToString = currentTime.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
//    int dateToInt = Integer.parseInt(timeToString);
//    log.info("the checked date is: {}", timeToString);
//    if (dateToInt >= 20171121 && dateToInt <= 20171127) {
//      for (String subscriber : subscriberIds) {
//        try {
//          String reply = "party?";
//          if (!db.isRegistered(subscriber.trim())) {
//            PushMessage pushMessage = new PushMessage(subscriber.trim(), new TextMessage(reply));
//            lineMessagingClient.pushMessage(pushMessage);
//          }
//        } catch (Exception e) {
//          log.info("Failed to push message: {}", e.toString());
//        }
//      }
//    }
//    if (dateToInt == 20171122){
//      for (String subscriber: subscriberIds){
//        try {
//          String reply = "party tmr!!!";
//          if (db.isRegistered(subscriber.trim())) {
//            PushMessage pushMessage = new PushMessage(subscriber.trim(), new TextMessage(reply));
//            lineMessagingClient.pushMessage(pushMessage);
//          }
//        } catch (Exception e) {
//          log.info("Failed to push message: {}", e.toString());
//        }
//      }
//    }
  }
}
