package com.aws.apigateway;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import java.util.logging.Logger;
import com.google.gson.Gson;
import java.util.Collections;
import java.util.Map;

public class APIGatewayLambdaFunction implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {
    private static final Logger LOGGER = Logger.getLogger(APIGatewayLambdaFunction.class.getName());
    private Gson gson = new Gson();
    
    @Override
    public String handleRequest(APIGatewayProxyRequestEvent request, Context context) {
        LOGGER.info("request: " + gson.toJson(request));

        Map<String, String> queryStringParameters = request.getQueryStringParameters();
        Map<String, String> headers = request.getHeaders();
        String body = request.getBody();

        String name = "you";
        String city = "World";
        String time = "day";
        String day = "";
        int responseCode = 200;

        if(queryStringParameters != null && !queryStringParameters.isEmpty()) {

            if(!queryStringParameters.get("name").isEmpty()) {
                LOGGER.info("Received name: " + queryStringParameters.get("name"));
                name = queryStringParameters.get("name");
            }

            if(!queryStringParameters.get("city").isEmpty()) {
                city = queryStringParameters.get("city");
            }
        }

        if(headers != null && !headers.isEmpty()) {
            LOGGER.info("Received day: " + headers.get("day"));
            day = headers.get("day");
        }

        // if(body != null && !body.isEmpty()) {
        //     RequestBody body = gson.fromJson(body, RequestBody.class);
        //     time = body.getTime();
        // }

        String greeting = String.format("Good %s, %s of %s.", time, name, city);
        if(!day.isEmpty()) {
            greeting += String.format(" Happy %s!", day);
        }

        ResponseBody responseBody = new ResponseBody(greeting, request);
        LOGGER.info("response: " + gson.toJson(responseBody));

        return new APIGatewayProxyResponseEvent()
            .withStatusCode(200)
            .withHeaders(Collections.singletonMap("Content-Type", "application/json"))
            .withBody(gson.toJson(responseBody));
    }
}