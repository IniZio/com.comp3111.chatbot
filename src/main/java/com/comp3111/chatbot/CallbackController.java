package com.comp3111.chatbot;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;

import com.linecorp.bot.model.action.*;
import com.linecorp.bot.model.message.template.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.google.common.io.ByteStreams;

import com.linecorp.bot.client.LineMessagingClient;
import com.linecorp.bot.client.MessageContentResponse;
import com.linecorp.bot.model.ReplyMessage;
import com.linecorp.bot.model.event.BeaconEvent;
import com.linecorp.bot.model.event.Event;
import com.linecorp.bot.model.event.FollowEvent;
import com.linecorp.bot.model.event.JoinEvent;
import com.linecorp.bot.model.event.MessageEvent;
import com.linecorp.bot.model.event.PostbackEvent;
import com.linecorp.bot.model.event.UnfollowEvent;
import com.linecorp.bot.model.event.message.AudioMessageContent;
import com.linecorp.bot.model.event.message.ImageMessageContent;
import com.linecorp.bot.model.event.message.LocationMessageContent;
import com.linecorp.bot.model.event.message.StickerMessageContent;
import com.linecorp.bot.model.event.message.TextMessageContent;
import com.linecorp.bot.model.event.message.VideoMessageContent;
import com.linecorp.bot.model.event.source.GroupSource;
import com.linecorp.bot.model.event.source.RoomSource;
import com.linecorp.bot.model.event.source.Source;
import com.linecorp.bot.model.message.AudioMessage;
import com.linecorp.bot.model.message.ImageMessage;
import com.linecorp.bot.model.message.ImagemapMessage;
import com.linecorp.bot.model.message.LocationMessage;
import com.linecorp.bot.model.message.Message;
import com.linecorp.bot.model.message.StickerMessage;
import com.linecorp.bot.model.message.TemplateMessage;
import com.linecorp.bot.model.message.TextMessage;
import com.linecorp.bot.model.message.VideoMessage;
import com.linecorp.bot.model.message.imagemap.ImagemapArea;
import com.linecorp.bot.model.message.imagemap.ImagemapBaseSize;
import com.linecorp.bot.model.message.imagemap.MessageImagemapAction;
import com.linecorp.bot.model.message.imagemap.URIImagemapAction;
import com.linecorp.bot.model.response.BotApiResponse;
import com.linecorp.bot.spring.boot.annotation.EventMapping;
import com.linecorp.bot.spring.boot.annotation.LineMessageHandler;

import lombok.NonNull;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;

import com.comp3111.chatbot.BusETARequestHandler;

@Slf4j
@LineMessageHandler
public class CallbackController {
    @Autowired
    private LineMessagingClient lineMessagingClient;
    // For People
    private static int number = 0;
    // For OpeningHours
    private static int tag = 0;

    @EventMapping
    public void handleTextMessageEvent(MessageEvent<TextMessageContent> event) throws Exception {
        TextMessageContent message = event.getMessage();
        handleTextContent(event.getReplyToken(), event, message);
    }

    @EventMapping
    public void handleStickerMessageEvent(MessageEvent<StickerMessageContent> event) {
        handleSticker(event.getReplyToken(), event.getMessage());
    }

    @EventMapping
    public void handleLocationMessageEvent(MessageEvent<LocationMessageContent> event) {
        LocationMessageContent locationMessage = event.getMessage();
        reply(event.getReplyToken(), new LocationMessage(
                locationMessage.getTitle(),
                locationMessage.getAddress(),
                locationMessage.getLatitude(),
                locationMessage.getLongitude()
        ));
    }

    @EventMapping
    public void handleImageMessageEvent(MessageEvent<ImageMessageContent> event) throws IOException {
        // You need to install ImageMagick
        handleHeavyContent(
                event.getReplyToken(),
                event.getMessage().getId(),
                responseBody -> {
                    DownloadedContent jpg = saveContent("jpg", responseBody);
                    DownloadedContent previewImg = createTempFile("jpg");
                    system(
                            "convert",
                            "-resize", "240x",
                            jpg.path.toString(),
                            previewImg.path.toString());
                    reply(((MessageEvent) event).getReplyToken(),
                          new ImageMessage(jpg.getUri(), jpg.getUri()));
                });
    }

    @EventMapping
    public void handleAudioMessageEvent(MessageEvent<AudioMessageContent> event) throws IOException {
        handleHeavyContent(
                event.getReplyToken(),
                event.getMessage().getId(),
                responseBody -> {
                    DownloadedContent mp4 = saveContent("mp4", responseBody);
                    reply(event.getReplyToken(), new AudioMessage(mp4.getUri(), 100));
                });
    }

    @EventMapping
    public void handleVideoMessageEvent(MessageEvent<VideoMessageContent> event) throws IOException {
        // You need to install ffmpeg and ImageMagick.
        handleHeavyContent(
                event.getReplyToken(),
                event.getMessage().getId(),
                responseBody -> {
                    DownloadedContent mp4 = saveContent("mp4", responseBody);
                    DownloadedContent previewImg = createTempFile("jpg");
                    system("convert",
                           mp4.path + "[0]",
                           previewImg.path.toString());
                    reply(((MessageEvent) event).getReplyToken(),
                          new VideoMessage(mp4.getUri(), previewImg.uri));
                });
    }

    @EventMapping
    public void handleUnfollowEvent(UnfollowEvent event) {
        log.info("unfollowed this bot: {}", event);
    }

    @EventMapping
    public void handleFollowEvent(FollowEvent event) {
        String replyToken = event.getReplyToken();
        this.replyText(replyToken, "Got followed event");
    }

    @EventMapping
    public void handleJoinEvent(JoinEvent event) {
        String replyToken = event.getReplyToken();
        this.replyText(replyToken, "Joined " + event.getSource());
    }

    @EventMapping
    public void handlePostbackEvent(PostbackEvent event) {
        String replyToken = event.getReplyToken();
        this.replyText(replyToken, "Got postback data " + event.getPostbackContent().getData() + ", param " + event.getPostbackContent().getParams().toString());
    }

    @EventMapping
    public void handleBeaconEvent(BeaconEvent event) {
        String replyToken = event.getReplyToken();
        this.replyText(replyToken, "Got beacon message " + event.getBeacon().getHwid());
    }

    @EventMapping
    public void handleOtherEvent(Event event) {
        log.info("Received message(Ignored): {}", event);
    }

    private void reply(@NonNull String replyToken, @NonNull Message message) {
        reply(replyToken, Collections.singletonList(message));
    }

    private void reply(@NonNull String replyToken, @NonNull List<Message> messages) {
        try {
            BotApiResponse apiResponse = lineMessagingClient
                    .replyMessage(new ReplyMessage(replyToken, messages))
                    .get();
            log.info("Sent messages: {}", apiResponse);
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    private void replyText(@NonNull String replyToken, @NonNull String message) {
        if (replyToken.isEmpty()) {
            throw new IllegalArgumentException("replyToken must not be empty");
        }
        if (message.length() > 1000) {
            message = message.substring(0, 1000 - 2) + "�色��";
        }
        this.reply(replyToken, new TextMessage(message));
    }

    private void handleHeavyContent(String replyToken, String messageId,
                                    Consumer<MessageContentResponse> messageConsumer) {
        final MessageContentResponse response;
        try {
            response = lineMessagingClient.getMessageContent(messageId)
                                          .get();
        } catch (InterruptedException | ExecutionException e) {
            reply(replyToken, new TextMessage("Cannot get image: " + e.getMessage()));
            throw new RuntimeException(e);
        }
        messageConsumer.accept(response);
    }

    private void handleSticker(String replyToken, StickerMessageContent content) {
        reply(replyToken, new StickerMessage(
                content.getPackageId(), content.getStickerId())
        );
    }

    private Boolean handleNextAction(String userId, String replyToken, String text, SQLDatabaseEngine db)
        throws Exception
    {
        // SQLDatabaseEngine db = new SQLDatabaseEngine();        
        String[] curr = db.nextAction(userId);
        String action = curr[1];
        String param = curr[0];
        log.info("Going to handle action {}, and it is {} null", action, action == null ? "" : "not");
        if (action == null || action.equals(ACTION.EXIT_MAIN)) {
            return false;
        }

        // Exit from action to main menu
        if (text.equals("exit")) {
            db.storeAction(userId, text, ACTION.EXIT_MAIN);
            handleNextAction(userId, replyToken, text, db);
        }

        try {
            switch (action) {
                case ACTION.PEOPLE_INPUT: {
                    String reply ="Who do you want to find? Please enter his/her full name or ITSC.";
                    this.replyText(replyToken, reply);
                    db.storeAction(userId, text, ACTION.PEOPLE_SEARCH);
                    break;
                }
                case ACTION.PEOPLE_SEARCH: {
                    String replyPeople;
                    
                    URLConnectionReader search = new URLConnectionReader();
                    PeopleList result=search.SearchPeople(text);
                    ArrayList<people> resultList = result.getList();
                        
                    StringBuilder results = new StringBuilder();
                    results.append("Search Result(s):");
                        
                    if (resultList ==null) {
                        results.append("\nNot found.");
                        this.replyText(replyToken, results.toString());
                        break;
                    }
                    else{
                        for (people p : resultList){
                            results.append(
                                String.format("\n\nTitle: %s\nName: %s\nEmail: %s\nPhone: %s\nDepartment: %s\nRoom: %s",
                                    p.getTitle(),
                                    p.getName(),
                                    p.getEmail(),
                                    p.getPhone(),
                                    p.getDepartment(),
                                    p.getRoom()
                                )
                            );
                        }
                    }
                    
                    if (PeopleList.too_many==true) {
                        results.append("\nToo many results...");
                    }
                    replyPeople = results.toString();
                    this.replyText(replyToken, replyPeople);
                    db.storeAction(userId, text, ACTION.EXIT_MAIN);
                    break;
                }
                case ACTION.ROOM_INPUT: {
                    String reply ="What room do you want to find? Please enter the room number.";                    
                    this.replyText(replyToken, reply);
                    db.storeAction(userId, text, ACTION.ROOM_SEARCH);
                    break;
                }
                case ACTION.ROOM_SEARCH: {
                    String reply;
                    try {
                        LiftAdvisor liftAdvisor = new LiftAdvisor(text);
                        if (liftAdvisor.noRoomNumberDetected()){
                            reply = "No room number detected. Please enter number along with keyword room or rm";
                            this.replyText(replyToken, reply);
                            break;
                        }
                        reply = liftAdvisor.getReplyMessage();
                    }catch (Exception e){
                        reply = "error";
                    }
                    this.replyText(replyToken, reply);
                    db.storeAction(userId, text, ACTION.EXIT_MAIN);                   
                    break;
                }
                case ACTION.OPENINGHOUR_CHOOSE: {
                    String reply = "Please enter the number in front of the facilities to query the opening hour:\n";
                    try {
                        reply += db.showFacilitiesChoices();
                    } catch (Exception e) {
                        reply = "Exception occur";
                    }
                    log.info("Returns echo message {}: {}", replyToken, reply);
                    this.replyText(replyToken, reply);
                    db.storeAction(userId, text, ACTION.OPENINGHOUR_SEARCH);                                       
                    break;
                }
                case ACTION.OPENINGHOUR_SEARCH: {
                    String reply;
                    try {
                        reply = db.openingHourSearch(text);
                    } catch (Exception e) {
                        reply = "Cannot find given facility";
                    }
                    this.replyText(replyToken, reply);
                    db.storeAction(userId, text, ACTION.EXIT_MAIN);
                    break;
                }
                case ACTION.BUS_CHOOSE_BUS: {
                    String reply ="Choose which bus you would like to take.";
                    ConfirmTemplate busConfirmTemplate = new ConfirmTemplate("Which route?",
                        new MessageAction("91", "91"),
                        new MessageAction("91M", "91M")
                    );
                    TemplateMessage busTemplateMessage = new TemplateMessage("Please type in 91 or 91M", busConfirmTemplate);
                    this.reply(replyToken, busTemplateMessage);
                    db.storeAction(userId, text, ACTION.BUS_CHOOSE_DEST);
                    break;
                }
                case ACTION.BUS_CHOOSE_DEST: {
                    switch (text) {
                        case "91":{
                            ConfirmTemplate route91ConfirmTemplate = new ConfirmTemplate("91 to which direction?",
                                    new MessageAction("Diamond Hill", "Diamond Hill"),
                                    new MessageAction("Clear Water Bay", "Clear Water Bay")
                            );
                            TemplateMessage route91TemplateMessage = new TemplateMessage("Please Type in Diamond Hill or Clear Water Bay", route91ConfirmTemplate);
                            this.reply(replyToken, route91TemplateMessage);
                            db.storeAction(userId, text, ACTION.BUS_SEARCH);
                            break;
                        }
                        case "91m":{
                            ConfirmTemplate route91MConfirmTemplate = new ConfirmTemplate("91M to which direction?",
                                    new MessageAction("Diamond Hill", "Diamond Hill"),
                                    new MessageAction("Po Lam", "Po Lam")
                            );
                            TemplateMessage route91MTemplateMessage = new TemplateMessage("Please Type in Diamond Hill or Po Lam", route91MConfirmTemplate);
                            this.reply(replyToken, route91MTemplateMessage);
                            db.storeAction(userId, text, ACTION.BUS_SEARCH);
                            break;
                        }
                        default: {
                            String reply = "Invalid bus number.";
                            this.replyText(replyToken, reply);
                            db.storeAction(userId, text, ACTION.BUS_CHOOSE_BUS);
                            handleNextAction(userId, replyToken, text, db);
                            return true;
                        }
                    }
                    break;
                }
                case ACTION.BUS_SEARCH: {
                    switch (param + " to " + text) {
                        case "91 to diamond hill":{
                            String replyMessage;
                            try {
                                BusETARequestHandler busETARequestHandler = new BusETARequestHandler("91", "1");
                                String results = "";
                                results = results + "Time: ";
                                results = results + busETARequestHandler.getReplyMessage();
                                replyMessage = results;
                            } catch (Exception e){
                                replyMessage = "error";
                            }
                            this.replyText(replyToken, replyMessage);
                            db.storeAction(userId, text, ACTION.EXIT_MAIN);
                            break;
                        }
                        case "91m to diamond hill":{
                            String replyMessage;
                            try {
                                BusETARequestHandler busETARequestHandler = new BusETARequestHandler("91M", "1");
                                String results = "";
                                results = results + "Time: ";
                                results = results + busETARequestHandler.getReplyMessage();
                                replyMessage = results;
                            } catch (Exception e){
                                replyMessage = "error";
                            }
                            this.replyText(replyToken, replyMessage);
                            db.storeAction(userId, text, ACTION.EXIT_MAIN);
                            break;
                        }
                        case "91 to clear water bay":{
                            String replyMessage;
                            try {
                                BusETARequestHandler busETARequestHandler = new BusETARequestHandler("91", "2");
                                String results = "";
                                results = results + "Time: ";
                                results = results + busETARequestHandler.getReplyMessage();
                                replyMessage = results;
                            } catch (Exception e){
                                replyMessage = "error";
                            }
                            this.replyText(replyToken, replyMessage);
                            db.storeAction(userId, text, ACTION.EXIT_MAIN);
                            break;
                        }
                        case "91m to po lam":{
                            String replyMessage;
                            try {
                                BusETARequestHandler busETARequestHandler = new BusETARequestHandler("91M", "2");
                                String results = "";
                                results = results + "Time: ";
                                results = results + busETARequestHandler.getReplyMessage();
                                replyMessage = results;
                            } catch (Exception e){
                                replyMessage = "error";
                            }
                            this.replyText(replyToken, replyMessage);
                            db.storeAction(userId, text, ACTION.EXIT_MAIN);
                            break;
                        }
                    }
                    break;
                }
                case ACTION.EXIT_MAIN: {
                    // TODO: print main menu
                    break;
                }
                default: {
                    return false;
                }
            }
        } catch (Exception e) {
            log.info(e.toString());
        }
        return true;
    }

    private void handleTextContent(String replyToken, Event event, TextMessageContent content)
            throws Exception {
    	
        String text = content.getText();
        text=text.toLowerCase();        
        
        log.info("Got text message from {}: {}", replyToken, text);

        // 1. Check for stored next action...
        String userId = event.getSource().getUserId();			
        SQLDatabaseEngine db = new SQLDatabaseEngine();

        // ... then leave if the action is done
        if (handleNextAction(userId, replyToken, text, db))
            return;

        // 2. If no matching previous action, determine action type based on input
        String reply = "";
        switch (text) {
            case "profile": {
                //String userId = event.getSource().getUserId();
                if (userId != null) {
                    lineMessagingClient
                            .getProfile(userId)
                            .whenComplete((profile, throwable) -> {
                                if (throwable != null) {
                                    this.replyText(replyToken, throwable.getMessage());
                                    return;
                                }

                                this.reply(
                                        replyToken,
                                        Arrays.asList(new TextMessage(
                                                              "Display name: " + profile.getDisplayName()),
                                                      new TextMessage("Status message: "
                                                                      + profile.getStatusMessage()))
                                );

                            });
                } else {
                    this.replyText(replyToken, "Bot can't use profile API without user ID");
                }
                break;
            }
            
            case "b":		//provide facilities time
                try { db.storeAction(userId, text, ACTION.OPENINGHOUR_CHOOSE); } catch (Exception e) {log.info(e.toString());}
                handleNextAction(userId, replyToken, text, db);
                break;

            // case "c":		// suggestedLinks
            //     reply ="Which link do you want to find?\n"
            //     +"1) Register for a locker\n"
            //     +"2) Register for courses\n"
            //     +"3) Check grades\n"
            //     +"4) Find school calendar\n"
            //     +"5) Book library rooms\n"
            //     +"6) Find lecture materials\n";
                
            //     this.replyText(
            //         replyToken,
            //         reply
            //         );
            //         // get input and search for links
            //     break;
            
            case "d":		//find people
                try { db.storeAction(userId, text, ACTION.PEOPLE_INPUT); } catch (Exception e) {log.info(e.toString());}
                handleNextAction(userId, replyToken, text, db);
                break;

            case "e":
                try { db.storeAction(userId, text, ACTION.ROOM_INPUT); } catch (Exception e) {log.info(e.toString());}
                handleNextAction(userId, replyToken, text, db);
                break;
            
            case "f":
                try { db.storeAction(userId, text, ACTION.BUS_CHOOSE_BUS); } catch (Exception e) {log.info(e.toString());}
                handleNextAction(userId, replyToken, text, db);
                break;

            default:
                String default_reply ="Which information do you want to know?\n"
                    +"a) Course information\n"
                    +"b) Restaurant/Facilities opening hours\n"
                    +"c) Links suggestions\n"
                    +"d) Find people\n"
                    +"e) Lift advisor\n"
                    +"f) Bus arrival/Departure time\n"
                    +"g) Deadline list\n"
                    +"h) Set notifications\n";
                log.info("Returns  message {}: {}", replyToken, default_reply);
                this.replyText(
                        replyToken,
                        default_reply
                );
                break;
        }
    }

    private static String createUri(String path) {
        return ServletUriComponentsBuilder.fromCurrentContextPath()
                                          .path(path).build()
                                          .toUriString();
    }

    private void system(String... args) {
        ProcessBuilder processBuilder = new ProcessBuilder(args);
        try {
            Process start = processBuilder.start();
            int i = start.waitFor();
            log.info("result: {} =>  {}", Arrays.toString(args), i);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        } catch (InterruptedException e) {
            log.info("Interrupted", e);
            Thread.currentThread().interrupt();
        }
    }

    private static DownloadedContent saveContent(String ext, MessageContentResponse responseBody) {
        log.info("Got content-type: {}", responseBody);

        DownloadedContent tempFile = createTempFile(ext);
        try (OutputStream outputStream = Files.newOutputStream(tempFile.path)) {
            ByteStreams.copy(responseBody.getStream(), outputStream);
            log.info("Saved {}: {}", ext, tempFile);
            return tempFile;
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private static DownloadedContent createTempFile(String ext) {
        String fileName = LocalDateTime.now().toString() + '-' + UUID.randomUUID().toString() + '.' + ext;
        Path tempFile = ChatbotApplication.downloadedContentDir.resolve(fileName);
        tempFile.toFile().deleteOnExit();
        return new DownloadedContent(
                tempFile,
                createUri("/downloaded/" + tempFile.getFileName()));
    }

    @Value
    public static class DownloadedContent {
        Path path;
        String uri;
    }
    
	public CallbackController() {
		database = new SQLDatabaseEngine();
		
	}

	private SQLDatabaseEngine database;
	
}
