package com.aws.bot.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.document.*;
import com.amazonaws.services.dynamodbv2.model.*;

public class DynamodbServiceTest {
    private DynamodbService dynamodbService;
    private Table table;
    private Config config;

    @Before
    public void setup() {
        InputStream configStream = getClass().getResourceAsStream("config/application.conf");
        config = ConfigFactory.parseReader(new InputStreamReader(configStream));

        dynamodbService = new DynamodbService();
        amazonDynamoDB = mock(AmazonDynamoDB.class);
        dynamoDB = new DynamoDB(amazonDynamoDB);
        table = dynamoDB.getTable(config.getString("table_name"));
    }

    @Test
    public void testWriteItem() {
        //Define item attributes
        Map<String, AttributeValue> itemAttributes = new HashMap<>();
        itemAttributes.put("name", "John Doe");
        itemAttributes.put("email", "john.doe@email.com");
        itemAttributes.put("phone", "(123)456-7890");

        // Define put item request
        PutItemRequest putItemRequest = new PutItemRequest()
            .withTableName(config.getString("table_name"));
            .withItem(itemAttributes);

        //Mock response from DynamoDB
        when(amazonDynamoDB.putItem(putItemRequest)).thenReturn(null);

        dynamodbService.writeItem("John Doe", "john.doe@email.com", "(123)456-7890");
        assertNotNull(table.getItem("name", "John Doe"));
    }
}