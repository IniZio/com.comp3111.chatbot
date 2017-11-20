package com.comp3111.chatbot;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.InputStream;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.WebApplicationContext;

import com.google.common.io.ByteStreams;

import com.linecorp.bot.client.LineMessagingClient;
import com.linecorp.bot.model.ReplyMessage;
import com.linecorp.bot.model.event.Event;
import com.linecorp.bot.model.event.FollowEvent;
import com.linecorp.bot.model.event.MessageEvent;
import com.linecorp.bot.model.event.message.MessageContent;
import com.linecorp.bot.model.event.message.TextMessageContent;
import com.linecorp.bot.model.message.TextMessage;
import com.linecorp.bot.spring.boot.annotation.LineBotMessages;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import static java.util.Collections.singletonList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.Collections;
import java.util.concurrent.CompletableFuture;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.linecorp.bot.model.event.source.GroupSource;
import com.linecorp.bot.model.event.source.UserSource;
import com.linecorp.bot.model.response.BotApiResponse;

import com.comp3111.chatbot.*;;

@RunWith(MockitoJUnitRunner.class)
@SpringBootTest(classes = { CallbackTester.class, CallbackController.class, SQLDatabaseEngine.class, ACTION.class,
                BusETARequestHandler.class, LiftAdvisor.class, people.class, PeopleList.class,
                URLConnectionReader.class })
public class CallbackTester {

        private String messageId = "325708";
        private String userId = "ffffff45d24edc510b079fdff54e4d0b";
        private String token = "ffffWiB7yP5Zw52FIkcQobQuGDXCTA";
        @Mock
        private LineMessagingClient lineMessagingClient;
        @InjectMocks
        private CallbackController underTest;

        @Test
        public void testFollow() throws Exception {
                FollowEvent follow = new FollowEvent(token, new UserSource(userId), Instant.now());
                underTest.handleFollowEvent(follow);
        }

        @Test
        public void testMenu() throws Exception {

                final MessageEvent request = new MessageEvent<TextMessageContent>(token, // reply token
                                new UserSource(userId), // userId
                                new TextMessageContent(messageId, "helloooooo"), //messageId , message
                                Instant.now() //timestamp
                );

                underTest.handleTextMessageEvent(request);

        }

        @Test
        public void testCourseNotFound() throws Exception {

                MessageEvent request = new MessageEvent<TextMessageContent>(token, new UserSource(userId),
                                new TextMessageContent(messageId, "a"), Instant.now());

                underTest.handleTextMessageEvent(request);

                request = new MessageEvent<TextMessageContent>(token, new UserSource(userId),
                                new TextMessageContent(messageId, "COMP3911"), Instant.now());

                underTest.handleTextMessageEvent(request);
        }

        @Test
        public void testCourseFoundOverview() throws Exception {

                MessageEvent request = new MessageEvent<TextMessageContent>(token, new UserSource(userId),
                                new TextMessageContent(messageId, "a"), Instant.now());

                underTest.handleTextMessageEvent(request);

                request = new MessageEvent<TextMessageContent>(token, new UserSource(userId),
                                new TextMessageContent(messageId, "COMP3111"), Instant.now());

                underTest.handleTextMessageEvent(request);

                request = new MessageEvent<TextMessageContent>(token, new UserSource(userId),
                                new TextMessageContent(messageId, "Course Overview for COMP3111"), Instant.now());

                underTest.handleTextMessageEvent(request);
        }

        @Test
        public void testCourseFoundQuota() throws Exception {

                MessageEvent request = new MessageEvent<TextMessageContent>(token, new UserSource(userId),
                                new TextMessageContent(messageId, "a"), Instant.now());

                underTest.handleTextMessageEvent(request);

                request = new MessageEvent<TextMessageContent>(token, new UserSource(userId),
                                new TextMessageContent(messageId, "COMP3111"), Instant.now());

                underTest.handleTextMessageEvent(request);

                request = new MessageEvent<TextMessageContent>(token, new UserSource(userId),
                                new TextMessageContent(messageId, "Quota of COMP3111"), Instant.now());

                underTest.handleTextMessageEvent(request);
        }

        @Test
        public void testCourseFoundSchdules() throws Exception {

                MessageEvent request = new MessageEvent<TextMessageContent>(token, new UserSource(userId),
                                new TextMessageContent(messageId, "a"), Instant.now());

                underTest.handleTextMessageEvent(request);

                request = new MessageEvent<TextMessageContent>(token, new UserSource(userId),
                                new TextMessageContent(messageId, "COMP3111"), Instant.now());

                underTest.handleTextMessageEvent(request);

                request = new MessageEvent<TextMessageContent>(token, new UserSource(userId),
                                new TextMessageContent(messageId, "Schedules for COMP3111"), Instant.now());

                underTest.handleTextMessageEvent(request);
        }

        @Test
        public void testCourseFoundQuotaLong() throws Exception {

                MessageEvent request = new MessageEvent<TextMessageContent>(token, new UserSource(userId),
                                new TextMessageContent(messageId, "a"), Instant.now());

                underTest.handleTextMessageEvent(request);

                request = new MessageEvent<TextMessageContent>(token, new UserSource(userId),
                                new TextMessageContent(messageId, "LANG1002S"), Instant.now());

                underTest.handleTextMessageEvent(request);

                request = new MessageEvent<TextMessageContent>(token, new UserSource(userId),
                                new TextMessageContent(messageId, "Quota of LANG1002S"), Instant.now());

                underTest.handleTextMessageEvent(request);
        }

        @Test
        public void testCourseFoundSchdulesLong() throws Exception {

                MessageEvent request = new MessageEvent<TextMessageContent>(token, new UserSource(userId),
                                new TextMessageContent(messageId, "a"), Instant.now());

                underTest.handleTextMessageEvent(request);

                request = new MessageEvent<TextMessageContent>(token, new UserSource(userId),
                                new TextMessageContent(messageId, "LANG1002S"), Instant.now());

                underTest.handleTextMessageEvent(request);

                request = new MessageEvent<TextMessageContent>(token, new UserSource(userId),
                                new TextMessageContent(messageId, "Schedules for LANG1002S"), Instant.now());

                underTest.handleTextMessageEvent(request);
        }

        @Test
        public void testCourseFoundProblematicInput() throws Exception {

                MessageEvent request = new MessageEvent<TextMessageContent>(token, new UserSource(userId),
                                new TextMessageContent(messageId, "a"), Instant.now());

                underTest.handleTextMessageEvent(request);

                request = new MessageEvent<TextMessageContent>(token, new UserSource(userId),
                                new TextMessageContent(messageId, "COMP3111"), Instant.now());

                underTest.handleTextMessageEvent(request);

                request = new MessageEvent<TextMessageContent>(token, new UserSource(userId),
                                new TextMessageContent(messageId, "sth about COMP3111"), Instant.now());

                underTest.handleTextMessageEvent(request);
        }

        @Test
        public void testLiftFound() throws Exception {

                MessageEvent request = new MessageEvent<TextMessageContent>(token, new UserSource(userId),
                                new TextMessageContent(messageId, "e"), Instant.now());

                underTest.handleTextMessageEvent(request);

                request = new MessageEvent<TextMessageContent>(token, new UserSource(userId),
                                new TextMessageContent(messageId, "Rm2465"), Instant.now());

                underTest.handleTextMessageEvent(request);
        }

        @Test
        public void testLiftNotFound() throws Exception {

                MessageEvent request = new MessageEvent<TextMessageContent>(token, new UserSource(userId),
                                new TextMessageContent(messageId, "e"), Instant.now());

                underTest.handleTextMessageEvent(request);

                request = new MessageEvent<TextMessageContent>(token, new UserSource(userId),
                                new TextMessageContent(messageId, "lsk"), Instant.now());

                underTest.handleTextMessageEvent(request);

                request = new MessageEvent<TextMessageContent>(token, new UserSource(userId),
                                new TextMessageContent(messageId, "ROOM 4010"), Instant.now());

                underTest.handleTextMessageEvent(request);
        }

        @Test
        public void testFacilities() throws Exception {

                MessageEvent request = new MessageEvent<TextMessageContent>(token, new UserSource(userId),
                                new TextMessageContent(messageId, "b"), Instant.now());

                underTest.handleTextMessageEvent(request);

                request = new MessageEvent<TextMessageContent>(token, new UserSource(userId),
                                new TextMessageContent(messageId, "abc"), Instant.now());

                underTest.handleTextMessageEvent(request);

                request = new MessageEvent<TextMessageContent>(token, new UserSource(userId),
                                new TextMessageContent(messageId, "b"), Instant.now());

                underTest.handleTextMessageEvent(request);

                request = new MessageEvent<TextMessageContent>(token, new UserSource(userId),
                                new TextMessageContent(messageId, "2"), Instant.now());

                underTest.handleTextMessageEvent(request);
        }

        @Test
        public void testPeople() throws Exception {

                MessageEvent request = new MessageEvent<TextMessageContent>(token, new UserSource(userId),
                                new TextMessageContent(messageId, "d"), Instant.now());

                underTest.handleTextMessageEvent(request);

                request = new MessageEvent<TextMessageContent>(token, new UserSource(userId),
                                new TextMessageContent(messageId, "kevin"), Instant.now());

                underTest.handleTextMessageEvent(request);

                request = new MessageEvent<TextMessageContent>(token, new UserSource(userId),
                                new TextMessageContent(messageId, "d"), Instant.now());

                underTest.handleTextMessageEvent(request);

                request = new MessageEvent<TextMessageContent>(token, new UserSource(userId),
                                new TextMessageContent(messageId, "sdfsdf"), Instant.now());

                underTest.handleTextMessageEvent(request);
        }

        @Test
        public void testBus91() throws Exception {

                MessageEvent request = new MessageEvent<TextMessageContent>(token, new UserSource(userId),
                                new TextMessageContent(messageId, "f"), Instant.now());

                underTest.handleTextMessageEvent(request);

                request = new MessageEvent<TextMessageContent>(token, new UserSource(userId),
                                new TextMessageContent(messageId, "792M"), Instant.now());

                underTest.handleTextMessageEvent(request);

                request = new MessageEvent<TextMessageContent>(token, new UserSource(userId),
                                new TextMessageContent(messageId, "f"), Instant.now());

                underTest.handleTextMessageEvent(request);

                request = new MessageEvent<TextMessageContent>(token, new UserSource(userId),
                                new TextMessageContent(messageId, "91"), Instant.now());

                underTest.handleTextMessageEvent(request);

                request = new MessageEvent<TextMessageContent>(token, new UserSource(userId),
                                new TextMessageContent(messageId, "diamond hill"), Instant.now());

                underTest.handleTextMessageEvent(request);

                request = new MessageEvent<TextMessageContent>(token, new UserSource(userId),
                                new TextMessageContent(messageId, "f"), Instant.now());

                underTest.handleTextMessageEvent(request);

                request = new MessageEvent<TextMessageContent>(token, new UserSource(userId),
                                new TextMessageContent(messageId, "91"), Instant.now());

                underTest.handleTextMessageEvent(request);

                request = new MessageEvent<TextMessageContent>(token, new UserSource(userId),
                                new TextMessageContent(messageId, "clear water bay"), Instant.now());

                underTest.handleTextMessageEvent(request);
        }

        @Test
        public void testBus91m() throws Exception {

                MessageEvent request = new MessageEvent<TextMessageContent>(token, new UserSource(userId),
                                new TextMessageContent(messageId, "f"), Instant.now());

                underTest.handleTextMessageEvent(request);

                request = new MessageEvent<TextMessageContent>(token, new UserSource(userId),
                                new TextMessageContent(messageId, "91m"), Instant.now());

                underTest.handleTextMessageEvent(request);

                request = new MessageEvent<TextMessageContent>(token, new UserSource(userId),
                                new TextMessageContent(messageId, "po lam"), Instant.now());

                underTest.handleTextMessageEvent(request);

                request = new MessageEvent<TextMessageContent>(token, new UserSource(userId),
                                new TextMessageContent(messageId, "f"), Instant.now());

                underTest.handleTextMessageEvent(request);

                request = new MessageEvent<TextMessageContent>(token, new UserSource(userId),
                                new TextMessageContent(messageId, "91m"), Instant.now());

                underTest.handleTextMessageEvent(request);

                request = new MessageEvent<TextMessageContent>(token, new UserSource(userId),
                                new TextMessageContent(messageId, "diamond hill"), Instant.now());

                underTest.handleTextMessageEvent(request);
        }

        @Test
        public void testExit() throws Exception {

                MessageEvent request = new MessageEvent<TextMessageContent>(token, // reply token
                                new UserSource(userId), // userId
                                new TextMessageContent(messageId, "f"), //messageId , message
                                Instant.now() //timestamp
                );

                underTest.handleTextMessageEvent(request);

                request = new MessageEvent<TextMessageContent>(token, // reply token
                                new UserSource(userId), // userId
                                new TextMessageContent(messageId, "exit"), //messageId , message
                                Instant.now() //timestamp
                );
                underTest.handleTextMessageEvent(request);
        }
        
        @Test
        public void testLink() throws Exception {

                MessageEvent request = new MessageEvent<TextMessageContent>(token, new UserSource(userId),
                                new TextMessageContent(messageId, "c"), Instant.now());

                underTest.handleTextMessageEvent(request);

                request = new MessageEvent<TextMessageContent>(token, new UserSource(userId),
                                new TextMessageContent(messageId, "20"), Instant.now());

                underTest.handleTextMessageEvent(request);

                request = new MessageEvent<TextMessageContent>(token, new UserSource(userId),
                                new TextMessageContent(messageId, "c"), Instant.now());

                underTest.handleTextMessageEvent(request);

                request = new MessageEvent<TextMessageContent>(token, new UserSource(userId),
                                new TextMessageContent(messageId, "1"), Instant.now());

                underTest.handleTextMessageEvent(request);
        }
}
