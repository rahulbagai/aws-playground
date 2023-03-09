package com.aws.bot.api;

import java.util.Collections;
import java.util.List;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.aws.bot.dao.Contact;
import com.aws.bot.services.DynamoDBService;
import com.google.gson.Gson;

public class ListContacts implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    DynamoDBService dynamoDBService;
    Gson gson = new Gson();

    public ListContacts() {
        this.dynamoDBService = new DynamoDBService();
    }

    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent input, Context context) {
        LambdaLogger logger = context.getLogger();
        logger.log("request: " + gson.toJson(input));

        String httpMethod = input.getHttpMethod();
        String path = input.getPath();

        if(httpMethod.equals("GET") && path.equals("/contacts")) {
            return listContacts(input);
        } else if(httpMethod.equals("POST") && path.equals("/contacts")) {
            return createContact(input);
        } else {
            return new APIGatewayProxyResponseEvent()
            .withStatusCode(404)
            .withBody("Not found");
        }
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
