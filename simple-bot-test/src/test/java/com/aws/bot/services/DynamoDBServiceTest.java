package com.aws.bot.services;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatcher;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.PutItemRequest;
import com.amazonaws.services.dynamodbv2.model.ScanRequest;
import com.amazonaws.services.dynamodbv2.model.ScanResult;
import com.aws.bot.config.AppConfig;

@RunWith(MockitoJUnitRunner.class)
public class DynamoDBServiceTest {

    @Mock
    private AmazonDynamoDBClient clientMock;
    private DynamoDBService service;
    private AppConfig appConfig;

    public DynamoDBServiceTest() {
        appConfig = new AppConfig();
    }

    @Before
    public void setUp() throws IOException {
        clientMock = mock(AmazonDynamoDBClient.class);
        service = new DynamoDBService(clientMock);
    }

    @Test
    public void testWriteItem() {
        String name = "John Doe";
        String email = "johndoe@example.com";
        String phone = "555-1234";

        Map<String, AttributeValue> item = new HashMap<>();
        item.put("name", new AttributeValue().withS(name));
        item.put("email", new AttributeValue().withS(email));
        item.put("phone", new AttributeValue().withS(phone));

        service.writeItem(name, email, phone);

        verify(clientMock).putItem(argThat(new PutItemRequestMatcher(appConfig.getDynamodbTableName(), item)));
    }

    @Test
    public void listItemsTest() {
        List<Map<String, AttributeValue>> results = new ArrayList<>();

        Map<String, AttributeValue> res1 = new HashMap<>();
        res1.put("name", new AttributeValue().withS("John"));
        res1.put("email", new AttributeValue().withS("john@example.com"));
        res1.put("phone", new AttributeValue().withS("123456789"));

        Map<String, AttributeValue> res2 = new HashMap<>();
        res2.put("name", new AttributeValue().withS("Beth"));
        res2.put("email", new AttributeValue().withS("beth@example.com"));
        res2.put("phone", new AttributeValue().withS("987654321"));

        results.add(res1);
        results.add(res2);

        when(clientMock.scan(any(ScanRequest.class))).thenReturn(new ScanResult().withItems(results));

        List<Map<String, String>> items = service.listItems();

        assertEquals("John", items.get(0).get("name"));
        assertEquals("john@example.com", items.get(0).get("email"));
        assertEquals("123456789", items.get(0).get("phone"));

        assertEquals("Beth", items.get(1).get("name"));
        assertEquals("beth@example.com", items.get(1).get("email"));
        assertEquals("987654321", items.get(1).get("phone"));
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
            return request.getTableName().equals(tableName) && Objects.deepEquals(request.getItem(), item);
        }
    }
}
