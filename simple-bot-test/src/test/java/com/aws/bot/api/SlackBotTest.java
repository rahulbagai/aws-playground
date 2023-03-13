package com.aws.bot.api;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.HashMap;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.slf4j.Logger;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.aws.bot.config.AppConfig;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.slack.api.RequestConfigurator;
import com.slack.api.Slack;
import com.slack.api.app_backend.SlackSignature;
import com.slack.api.methods.SlackApiException;
import com.slack.api.methods.request.chat.ChatPostMessageRequest;
import com.slack.api.methods.response.chat.ChatPostMessageResponse;

public class SlackBotTest {

    @Mock
    private AppConfig appConfig;

    @Mock
    private Context context;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private Slack slack;

    @Spy
    Logger logger;

    @InjectMocks
    SlackBot handler;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
        doReturn("SLACK_SIGNING_SECRET").when(appConfig).getSlackSigningSecret();
    }

    @Test
    @DisplayName("Test handleEvent with valid challenge request and signature")
    public void testHandleEventWithValidChallengeRequest() throws IOException, SlackApiException {
        APIGatewayProxyRequestEvent req = createApiRequest(false, false, true);
        APIGatewayProxyResponseEvent res = handler.handleEvent(req, context, slack);
        Assertions.assertEquals(200, res.getStatusCode());

        JsonParser parser = new JsonParser();
        JsonObject json = parser.parse(res.getBody()).getAsJsonObject();
        String challenge = json.get("challenge").getAsString();
        Assertions.assertEquals("testing", challenge);
    }

    @Test
    @DisplayName("Test handleEvent with invalid request signature")
    public void testHandleEventWithInvalidRequestSignature() throws IOException, SlackApiException {
        APIGatewayProxyRequestEvent req = createApiRequest(false, false, true);

        String timestamp = String.valueOf(System.currentTimeMillis() / 1000L);
        req.withHeaders(new HashMap<String, String>() {
            {
                put("X-Slack-Request-Timestamp", timestamp);
                put("X-Slack-Signature", "invalid");
            }
        });

        APIGatewayProxyResponseEvent res = handler.handleEvent(req, context, slack);
        Assertions.assertEquals(401, res.getStatusCode());
    }

    @Test
    @DisplayName("Test handleEvent with invalid request")
    public void testHandleEventWithInvalidRequest() throws IOException, SlackApiException {
        APIGatewayProxyRequestEvent req = createApiRequest(false, true, false);
        APIGatewayProxyResponseEvent res = handler.handleEvent(req, context, slack);
        Assertions.assertEquals(400, res.getStatusCode());
        Assertions.assertEquals("Unknown event type: random_event", res.getBody());
    }

    @Test
    @DisplayName("Ignore bot messages")
    public void testIgnoreBotMessages() throws Exception {
        APIGatewayProxyRequestEvent req = createApiRequest(true, false, false);
        APIGatewayProxyResponseEvent res = handler.handleEvent(req, context, slack);
        Assertions.assertEquals(200, res.getStatusCode());
        Assertions.assertEquals("{}", res.getBody());
    }

    @Test
    @DisplayName("Test chatPostMessage API call with valid channel and message")
    public void testChatPostMessageWithValidInputs() throws IOException, SlackApiException {
        APIGatewayProxyRequestEvent req = createApiRequest(false, false, false);

        // Mock the slack api call response object
        ChatPostMessageResponse mockResponse = new ChatPostMessageResponse();
        mockResponse.setOk(true);
        mockResponse.setChannel("channelId");

        when(slack.methods(anyString()).chatPostMessage(
                Mockito.<RequestConfigurator<ChatPostMessageRequest.ChatPostMessageRequestBuilder>>any()))
                .thenReturn(mockResponse);

        // Call the method that includes the slack API call with valid input values
        APIGatewayProxyResponseEvent response = handler.handleEvent(req, context, slack);

        // Check if the returned response has a status code '200' as expected
        Assertions.assertEquals(200, response.getStatusCode());

        // Check if the body of the response string contains the serialized mockup data
        // (chatPostMessageResponse) as expected.
        Assertions.assertTrue(response.getBody().equals(mockResponse.toString()));
    }

    @Test
    @DisplayName("Test chatPostMessage API call will error")
    public void testHandleError() throws Exception {
        APIGatewayProxyRequestEvent req = createApiRequest(false, false, false);

        ChatPostMessageResponse mockResponse = new ChatPostMessageResponse();
        mockResponse.setOk(false);
        mockResponse.setChannel("channelId");
        mockResponse.setError("Something went wrong!");

        when(slack.methods(anyString()).chatPostMessage(
                Mockito.<RequestConfigurator<ChatPostMessageRequest.ChatPostMessageRequestBuilder>>any()))
                .thenReturn(mockResponse);

        APIGatewayProxyResponseEvent res = handler.handleEvent(req, context, slack);

        Assertions.assertEquals(400, res.getStatusCode());
        Assertions.assertEquals("Something went wrong!", res.getBody());
    }

    private APIGatewayProxyRequestEvent createApiRequest(boolean isBot, boolean isRandomEvent, boolean isChallenge) {
        JsonObject requestBody = new JsonObject();

        if (isChallenge) {
            requestBody.addProperty("type", "url_verification");
            requestBody.addProperty("challenge", "testing");
        } else {
            if (!isRandomEvent) {
                requestBody.addProperty("type", "event_callback");
            } else {
                requestBody.addProperty("type", "random_event");
            }

            JsonObject event = new JsonObject();
            event.addProperty("channel", "CHANNEL_ID");

            if (isBot) {
                event.addProperty("bot_id", "BOT_ID");
            }
            requestBody.add("event", event);
        }

        String timestamp = String.valueOf(System.currentTimeMillis() / 1000L);
        String signature = new SlackSignature.Generator("SLACK_SIGNING_SECRET").generate(timestamp,
                requestBody.toString());

        APIGatewayProxyRequestEvent req = new APIGatewayProxyRequestEvent()
                .withBody(requestBody.toString());
        req.withHeaders(new HashMap<String, String>() {
            {
                put("X-Slack-Request-Timestamp", timestamp);
                put("X-Slack-Signature", signature);
            }
        });

        return req;
    }
}
