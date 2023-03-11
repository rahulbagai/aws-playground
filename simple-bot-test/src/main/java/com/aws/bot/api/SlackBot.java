package com.aws.bot.api;

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
import com.slack.api.app_backend.SlackSignature;

public class SlackBot {

    private final AppConfig appConfig = new AppConfig();
    private final Gson gson = new Gson();
    private static final Logger LOGGER = LoggerFactory.getLogger(SlackBot.class);

    /**
     * @param request
     * @param context
     * @return
     */
    public APIGatewayProxyResponseEvent handleEvent(APIGatewayProxyRequestEvent request, Context context) {
        String timestampHeader = request.getHeaders().get("X-Slack-Request-Timestamp");
        String signatureHeader = request.getHeaders().get("X-Slack-Signature");
        String requestBody = request.getBody();

        JsonParser parser = new JsonParser();
        JsonObject json = parser.parse(requestBody).getAsJsonObject();
        String challenge = json.get("challenge").getAsString();
        LOGGER.debug("challenge: " + challenge);

        // create new object for SlackSignature$Generator.class
        SlackSignature.Generator generator = new SlackSignature.Generator(appConfig.getSlackSigningSecret());
        String calculatedSignature = generator.generate(timestampHeader, requestBody);

        if (!calculatedSignature.equals(signatureHeader)) {
            LOGGER.error("Slack request not verified. Calculated: " + calculatedSignature + " | Received: "
                    + signatureHeader);
            return new APIGatewayProxyResponseEvent()
                    .withStatusCode(401)
                    .withBody("Slack request not verified");
        }

        LOGGER.debug("Slack request verified");
        APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent()
                .withStatusCode(200)
                .withBody("{\"challenge\":\"" + challenge + "\"}")
                .withHeaders(Collections.singletonMap("Content-type", "application/json"));

        LOGGER.debug("response: " + gson.toJson(response));
        return response;
    }
}
