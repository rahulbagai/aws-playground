package com.aws.bot.api;

import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.aws.bot.dao.Contact;
import com.aws.bot.services.DynamoDBService;
import com.google.gson.Gson;
import com.slack.api.Slack;

public class APILambda implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {
    private final DynamoDBService dynamoDBService = new DynamoDBService();
    private final SlackBot slackBot = new SlackBot();
    private final Slack slack = Slack.getInstance();
    private final Gson gson = new Gson();
    private static final Logger LOGGER = LoggerFactory.getLogger(APILambda.class);

    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent request, Context context) {
        try {
            LOGGER.debug("request: " + gson.toJson(request));

            String httpMethod = request.getHttpMethod();
            String path = request.getPath();
            LOGGER.debug(null, "HTTP Method: {}, Path: {}", httpMethod, path);

            if (httpMethod.equals("GET") && path.equals("/contacts")) {
                return listContacts(request);
            } else if (httpMethod.equals("POST") && path.equals("/contacts")) {
                return createContact(request);
            } else if (httpMethod.equals("POST") && path.equals("/slackbotevents")) {
                return slackBot.handleEvent(request, context, slack);
            }
        } catch (Exception ex) {
            LOGGER.error("Exception: {}", ex);
        }

        return new APIGatewayProxyResponseEvent()
                .withStatusCode(404)
                .withBody("Not found");
    }

    private APIGatewayProxyResponseEvent listContacts(APIGatewayProxyRequestEvent input) {
        List<Contact> contacts = dynamoDBService.listContacts();

        return new APIGatewayProxyResponseEvent()
                .withStatusCode(200)
                .withBody(gson.toJson(contacts))
                .withHeaders(Collections.singletonMap("Content-Type", "application/json"));
    }

    private APIGatewayProxyResponseEvent createContact(APIGatewayProxyRequestEvent input) {
        // Parse the request body to create a new contact
        Contact contact = gson.fromJson(input.getBody(), Contact.class);

        // Generate a random ID for the new contact
        contact.setId(Integer.toString((int) (Math.random() * 100000)));

        dynamoDBService.writeContact(contact.getName(), contact.getEmail(), contact.getPhone());

        // Return the new contact as JSON
        APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent();
        response.setStatusCode(200);
        return response;
    }
}
