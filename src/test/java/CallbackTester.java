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
import com.linecorp.bot.model.event.source.UserSource;
import com.linecorp.bot.model.message.TextMessage;
import com.linecorp.bot.spring.boot.annotation.LineBotMessages;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import java.time.Instant;


import com.linecorp.bot.model.event.message.MessageContent;
import com.linecorp.bot.model.event.source.Source;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = { CallbackTester.class, CallbackController.class, SQLDatabaseEngine.class })
public class CallbackTester {

	@Autowired
	private CallbackController callback;
	
	TextMessageContent tester = new TextMessageContent("ABCD", "hi");// id & message text
	Source source = new UserSource("ABCD");		//user id
	Instant now = Instant.now();		//timestamp
	String token = "xxxx";	//token
	
	
	MessageEvent<TextMessageContent> event = new MessageEvent<TextMessageContent> (token, source, tester, now);
	
	
	@Test
	public void testMenu() throws Exception {
		boolean thrown = false;
		try {
			callback.handleTextMessageEvent(event);
		} catch (Exception e) {
			thrown = true;
		}
		assertThat(!thrown);
	}
	
}
