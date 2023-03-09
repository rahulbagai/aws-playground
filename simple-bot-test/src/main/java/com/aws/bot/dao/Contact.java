package com.aws.bot.dao;

import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;

@DynamoDbBean
public class Contact {
    
    private String id;
    private String name;
    private String email;
    private String phone;
    
    public Contact() {}

    public Contact(String name, String email, String phone, String id) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
    }

    @DynamoDbPartitionKey
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

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

    @Override
    public String toString() {
        return "Contact [id=" + id + ", name=" + name + ", email=" + email + ", phone=" + phone + "]";
    }
}

    


