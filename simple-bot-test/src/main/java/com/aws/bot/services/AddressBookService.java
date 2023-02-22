package com.aws.bot.services;

import com.aws.bot.models.SuccesResponse;
import com.aws.bot.models.ErrorResponse;

public class AddressBookService {
    private DynamodbService dynamodbService;

    public AddressBookService(DynamodbService dynamodbService) {
        this.dynamodbService = dynamodbService;
    }

    public Map<String, String> addContact(String name, String emailAddress, String phoneNumber) {
        try {
            dynamodbService.write(name, emailAddress, phoneNumber);
            return Map.of("success", "The contact was successfully created!");
        }catch(Exception e) {
            return Map.of("error", "Failed to create contact.");
        }
    }

    public List<Map<String, String>> getContactList() {
        return dynamodbService.getData();
    }
}