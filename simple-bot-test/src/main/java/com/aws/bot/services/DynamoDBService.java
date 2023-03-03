package com.aws.bot.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aws.bot.config.AppConfig;
import com.aws.bot.dao.Contact;

public class DynamoDBService {
    private DynamoDbTable<Contact> table;

    public DynamoDBService() {
        AppConfig appConfig = new AppConfig();

        /*
         * Initalize dynamodb client and dynamodb table.
         */

         

    }

    // public void writeItem(String name, String email, String phone) {
    // Map<String, AttributeValue> item_values = new HashMap<>();
    // item_values.put("name", new AttributeValue().withS(name));
    // item_values.put("email", new AttributeValue().withS(email));
    // item_values.put("phone", new AttributeValue().withS(phone));

    // PutItemRequest request = new
    // PutItemRequest().withTableName(tableName).withItem(item_values);
    // client.putItem(request);
    // }

    /*
     * Write a function listItems which scans the dymamodb table and returns a
     * List<Map<String, String>> of all the items in the table.
     */

        
}
