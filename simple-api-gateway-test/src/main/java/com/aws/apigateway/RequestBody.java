package com.aws.apigateway;

class RequestBody {
    private String time;

    public RequestBody(String time) {
        this.time = time;
    }

    public String getTime() {
        return time;
    }
}