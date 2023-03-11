package com.aws.bot.api;

import static org.mockito.Mockito.doReturn;

import java.util.HashMap;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.slf4j.Logger;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.aws.bot.config.AppConfig;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.slack.api.app_backend.SlackSignature;

public class SlackBotTest {

    @Mock
    private AppConfig appConfig;

    @Mock
    private Context context;

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
    @DisplayName("Test handleEvent with valid request")
    public void testHandleEventWithValidRequest() {
        String requestBody = "{\"challenge\":\"testing\"}";
        APIGatewayProxyRequestEvent req = new APIGatewayProxyRequestEvent()
                .withBody(requestBody);

        String timestamp = String.valueOf(System.currentTimeMillis() / 1000L);
        String signature = new SlackSignature.Generator("SLACK_SIGNING_SECRET").generate(timestamp, requestBody);
        req.withHeaders(new HashMap<String, String>() {{
            put("X-Slack-Request-Timestamp", timestamp);
            put("X-Slack-Signature", signature);
        }});

        APIGatewayProxyResponseEvent res = handler.handleEvent(req, context);
        Assertions.assertEquals(200, res.getStatusCode());
        
        JsonParser parser = new JsonParser();
        JsonObject json = parser.parse(res.getBody()).getAsJsonObject();
        String challenge = json.get("challenge").getAsString();
        Assertions.assertEquals("testing", challenge);
    }

    @Test
    @DisplayName("Test handleEvent with invalid request signature")
    public void testHandleEventWithInvalidRequestSignature() {
        String requestBody = "{\"challenge\":\"testing\"}";
        APIGatewayProxyRequestEvent req = new APIGatewayProxyRequestEvent()
                .withBody(requestBody);

        String timestamp = String.valueOf(System.currentTimeMillis() / 1000L);
        req.withHeaders(new HashMap<String, String>() {{
            put("X-Slack-Request-Timestamp", timestamp);
            put("X-Slack-Signature", "invalid");
        }});

        APIGatewayProxyResponseEvent res = handler.handleEvent(req, context);
        Assertions.assertEquals(401, res.getStatusCode());
    }
}
