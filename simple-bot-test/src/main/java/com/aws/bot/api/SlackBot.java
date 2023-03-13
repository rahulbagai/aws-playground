package com.aws.bot.api;

import java.io.IOException;
import java.util.Collections;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.aws.bot.config.AppConfig;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.slack.api.Slack;
import com.slack.api.app_backend.SlackSignature;
import com.slack.api.methods.SlackApiException;
import com.slack.api.methods.response.chat.ChatPostMessageResponse;

public class SlackBot {

    private final AppConfig appConfig = new AppConfig();
    private final Gson gson = new Gson();
    private final JsonParser parser = new JsonParser();
    private static final Logger LOGGER = LoggerFactory.getLogger(SlackBot.class);

    /**
     * @param request
     * @param context
     * @return
     */
    public APIGatewayProxyResponseEvent handleEvent(APIGatewayProxyRequestEvent request, Context context, Slack slack)
            throws SlackApiException, IOException {
        String timestampHeader = request.getHeaders().get("X-Slack-Request-Timestamp");
        String signatureHeader = request.getHeaders().get("X-Slack-Signature");
        String requestBody = request.getBody();
        JsonObject requestBodyJson = parser.parse(requestBody).getAsJsonObject();

        if (!verifySlackRequest(requestBody, timestampHeader, signatureHeader)) {
            return new APIGatewayProxyResponseEvent()
                    .withStatusCode(401)
                    .withBody("Slack request not verified");
        }

        String eventType = requestBodyJson.get("type").getAsString();
        LOGGER.debug("eventType: " + eventType);

        switch (eventType) {
            case "url_verification":
                String challenge = requestBodyJson.get("challenge").getAsString();
                LOGGER.debug("challenge: " + challenge);

                APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent()
                        .withStatusCode(200)
                        .withBody("{\"challenge\":\"" + challenge + "\"}")
                        .withHeaders(Collections.singletonMap("Content-type", "application/json"));

                LOGGER.debug("response: " + gson.toJson(response));
                return response;
            case "event_callback":
                LOGGER.debug("event_callback");
                JsonObject event = requestBodyJson.get("event").getAsJsonObject();

                /*
                 * Ignore bot messages.
                 */
                if (event.has("bot_id")) {
                    LOGGER.debug("bot_id");
                    return new APIGatewayProxyResponseEvent()
                            .withStatusCode(200)
                            .withBody("{}");
                }

                String channel = event.get("channel").getAsString();
                ChatPostMessageResponse messageResponse = slack.methods(appConfig.getSlackToken())
                        .chatPostMessage(req -> req.channel(channel).text("Hello World!"));

                if (messageResponse.isOk()) {
                    return new APIGatewayProxyResponseEvent()
                            .withStatusCode(200)
                            .withBody(messageResponse.toString());
                } else {
                    return new APIGatewayProxyResponseEvent()
                            .withStatusCode(400)
                            .withBody(messageResponse.getError());
                }
            default:
                LOGGER.error("Unknown event type: " + eventType);
                return new APIGatewayProxyResponseEvent()
                        .withStatusCode(400)
                        .withBody("Unknown event type: " + eventType);
        }
    }

    private Boolean verifySlackRequest(String requestBody, String timestampHeader, String signatureHeader) {

        // create new object for SlackSignature$Generator.class
        SlackSignature.Generator generator = new SlackSignature.Generator(appConfig.getSlackSigningSecret());
        String calculatedSignature = generator.generate(timestampHeader, requestBody);

        if (!calculatedSignature.equals(signatureHeader)) {
            LOGGER.error("Slack request not verified. Calculated: " + calculatedSignature + " | Received: "
                    + signatureHeader);
            return false;
        }

        LOGGER.debug("Slack request verified");
        return true;
    }
}
