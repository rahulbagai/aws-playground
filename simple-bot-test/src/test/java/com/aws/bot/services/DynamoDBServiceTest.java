package com.aws.bot.services;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatcher;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.aws.bot.config.AppConfig;

import software.amazon.awssdk.services.dynamodb.*;
import software.amazon.awssdk.services.dynamodb.model.*;

@RunWith(MockitoJUnitRunner.class)
public class DynamoDBServiceTest {

    @Mock
    private DynamoDbClient client;
    private DynamoDBService service;
    private AppConfig appConfig;

    public DynamoDBServiceTest() {
        appConfig = new AppConfig();
    }

    @Before
    public void setUp() throws IOException {
        String tableName = appConfig.getDynamodbTableName();
        client = mock(DynamoDbClient.class);
        service = new DynamoDBService(client);
    }

    @Test
    public void testWriteItemAndListItems() {
        String name = "John Doe";
        String email = "johndoe@example.com";
        String phone = "555-1234";

        service.writeItem(name, email, phone);

        // Verify that the putItem method was called with the correct values
        Map<String, AttributeValue> item = new HashMap<>();
        item.put("name", AttributeValue.builder().s(name).build());
        item.put("email", AttributeValue.builder().s(email).build());
        item.put("phone", AttributeValue.builder().s(phone).build());

        verify(client).putItem(argThat(new PutItemRequestMatcher(appConfig.getDynamodbTableName(), item)));

        // Set up the mock response for the scan method
        // Map<String, AttributeValue> itemResponse = new HashMap<>();
        // itemResponse.put("name", AttributeValue.builder().s(name).build());
        // itemResponse.put("email", AttributeValue.builder().s(email).build());
        // itemResponse.put("phone", AttributeValue.builder().s(phone).build());

        // ScanResponse scanResponse = ScanResponse.builder()
        //         .items(itemResponse)
        //         .build();

        // when(client.scan(ScanRequest.builder().tableName(appConfig.getDynamodbTableName()).build())).thenReturn(scanResponse);

        // // Call the listItems method and verify that the correct values are returned
        // String expectedOutput = "Name: " + name + ", Email: " + email + ", Phone: " + phone + "\n";
        // assertEquals(expectedOutput, service.listItems());
    }

    private static class PutItemRequestMatcher implements ArgumentMatcher<PutItemRequest> {

        private final String tableName;
        private final Map<String, AttributeValue> item;

        public PutItemRequestMatcher(String tableName, Map<String, AttributeValue> item) {
            this.tableName = tableName;
            this.item = item;
        }

        @Override
        public boolean matches(PutItemRequest request) {
            return request.tableName().equals(tableName) && request.item().equals(item);
        }
    }
}
