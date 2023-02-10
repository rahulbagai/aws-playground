package com.aws.apigateway;

class ResponseBody {
    private String message;
    private APIGaatewayProxyRequestEvent event;

    public ResponseBody(String message, APIGaatewayProxyRequestEvent event) {
        this.message = message;
        this.event = event;
    }
}