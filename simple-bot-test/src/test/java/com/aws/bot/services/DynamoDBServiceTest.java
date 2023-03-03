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
import java.util.stream.Collectors;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatcher;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.PutItemRequest;
import com.amazonaws.services.dynamodbv2.model.ScanRequest;
import com.amazonaws.services.dynamodbv2.model.ScanResult;
import com.aws.bot.config.AppConfig;
import com.aws.bot.dao.Contact;

import software.amazon.awssdk.core.pagination.sync.SdkIterable;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.model.GetItemEnhancedRequest;

@RunWith(MockitoJUnitRunner.class)
public class DynamoDBServiceTest {
    @Mock
    private DynamoDbTable<Contact> tableMock;

    private DynamoDBService dynamodbService;

    @Before
    public void setUp() throws IOException {
        tableMock = mock(DynamoDbTable.class); 
    }

    // @Test
    // public void testWriteItem() {
    //     String name = "John Doe";
    //     String email = "johndoe@example.com";
    //     String phone = "555-1234";

    //     Map<String, AttributeValue> item = new HashMap<>();
    //     item.put("name", new AttributeValue().withS(name));
    //     item.put("email", new AttributeValue().withS(email));
    //     item.put("phone", new AttributeValue().withS(phone));

    //     service.writeItem(name, email, phone);

    //     verify(clientMock).putItem(argThat(new PutItemRequestMatcher(appConfig.getDynamodbTableName(), item)));
    // }

    /*
     * Write a unit test to test DynamodbService.listItems() method
     */
    @Test
    public void listItemsShouldReturnExpectedData() {
        List<Contact> contacts = new ArrayList<>();
        contacts.add(new Contact("John Doe", "XXXXXXXXXXXXXXXXXXX", "555-1234"));
        contacts.add(new Contact("Jane Doe", "XXXXXXXXXXXXXXXXXXX", "555-4321"));
        contacts.add(new Contact("Joe Doe", "XXXXXXXXXXXXXXXXXX", "555-5678"));

        when(tableMock.scan()).thenReturn(new SdkIterable<Contact>() {
            @Override
            public SdkIterable<Contact> page(int page) {
                return this;
            }

            @Override
            public List<Contact> collect() {
                return contacts;
            }
        });

        dynamodbService = new DynamoDBService(tableMock);
        List<Contact> result = dynamodbService.listItems();

        assertEquals(contacts, result);
    }








