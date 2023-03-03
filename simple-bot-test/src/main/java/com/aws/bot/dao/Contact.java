package com.aws.bot.dao;

import java.util.Map;

import com.amazonaws.services.dynamodbv2.model.AttributeValue;

import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;

@DynamoDbBean
public class Contact {
    private String name;
    private String email;
    private String phone;

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEmail() {
        return this.email;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPhone() {
        return this.phone;
    }

    // create toItem() method Contact Object to Map<String, AttributeValue>
    public  Map<String, AttributeValue> toItem() {
        return Map.of(
            "name", new AttributeValue().withS(name),
            "email", new AttributeValue().withS(email),
            "phone", new AttributeValue().withS(phone)
        );
    }
}
    


