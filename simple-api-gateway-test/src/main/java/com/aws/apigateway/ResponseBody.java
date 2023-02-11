package com.aws.apigateway;

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;

class ResponseBody {
    private String message;
    private APIGatewayProxyRequestEvent event;

    public ResponseBody(String message, APIGatewayProxyRequestEvent event) {
        this.message = message;
        this.event = event;
    }
}