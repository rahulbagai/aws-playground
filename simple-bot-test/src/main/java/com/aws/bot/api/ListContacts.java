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

public class ListContacts implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent>{

    DynamoDBService dynamoDBService;
    Gson gson = new Gson();
        
    public ListContacts() {
        this.dynamoDBService = new DynamoDBService();
    }

    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent input, Context context) {
        LambdaLogger logger = context.getLogger();
        logger.log("request: " + gson.toJson(input));

        //Fetch all the contacts with fields name, email, phone by making a DynamoDBService call.
        List<Contact> contacts = dynamoDBService.listItems();

        //Create response with status code 200, application/json header and body as JSON using gson library.
        APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent()
            .withStatusCode(200)
            .withBody(gson.toJson(contacts))
            .withHeaders(Collections.singletonMap("Content-Type", "application/json"));
         
        return response;
    }
}
