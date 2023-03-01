package com.aws.bot.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.aws.bot.config.AppConfig;

import com.amazonaws.services.dynamodbv2.*;
import com.amazonaws.services.dynamodbv2.model.*;

public class DynamoDBService {
    private AmazonDynamoDBClient client;
    private String tableName;

    public DynamoDBService(AmazonDynamoDBClient client) {
        this.client = client;
        
        AppConfig appConfig = new AppConfig();
        tableName = appConfig.getDynamodbTableName();
    }

    public void writeItem(String name, String email, String phone) {
        Map<String, AttributeValue> item_values = new HashMap<>();
        item_values.put("name", new AttributeValue().withS(name));
        item_values.put("email", new AttributeValue().withS(email));
        item_values.put("phone", new AttributeValue().withS(phone));

        PutItemRequest request = new PutItemRequest().withTableName(tableName).withItem(item_values);
        client.putItem(request);
    }

    public List<Map<String, String>> listItems() {
        ScanRequest scanRequest = new ScanRequest().withTableName(tableName);

        ScanResult scanResult = client.scan(scanRequest);
        List<Map<String, AttributeValue>> results = scanResult.getItems();

        List<Map<String, String>> items = new ArrayList<>();
        for(Map<String, AttributeValue> res : results) {
            Map<String, String> data = new HashMap<>();
            data.put("name", res.get("name").getS());
            data.put("email", res.get("email").getS());
            data.put("phone", res.get("phone").getS());
            
            items.add(data);
        }

        return items;
    }
}