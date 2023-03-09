package com.aws.bot.services;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.aws.bot.config.AppConfig;
import com.aws.bot.dao.Contact;

import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.DynamoDbException;

public class DynamoDBService {

    private DynamoDbEnhancedClient enhancedClient;
    private DynamoDbTable<Contact> table;

    public DynamoDBService() {
        AppConfig appConfig = new AppConfig();
        DynamoDbClient ddb = DynamoDbClient.builder().build();
        enhancedClient = DynamoDbEnhancedClient.builder().dynamoDbClient(ddb).build();
        table = enhancedClient.table(appConfig.getDynamodbTableName(), TableSchema.fromBean(Contact.class));
    }

    public DynamoDBService(DynamoDbEnhancedClient enhancedClient) {
        AppConfig appConfig = new AppConfig();
        table = enhancedClient.table(appConfig.getDynamodbTableName(), TableSchema.fromBean(Contact.class));
    }

    public DynamoDBService(DynamoDbTable<Contact> table) {
        this.table = table;
    }

    public void writeContact(String name, String email, String phone) {
        try {
            Contact c = new Contact(name, email, phone, generateId(name, email, phone));
            table.putItem(c);
        } catch(DynamoDbException e) {
            e.printStackTrace();
        }
    }

    public List<Contact> listContacts() {
        List<Contact> contacts = new ArrayList<>();

        Iterator<Contact> it = table.scan().items().iterator();
        while(it.hasNext()) {
            contacts.add(it.next());
        }

        return contacts;
    }

    /*
     * Generate a unique id for the contact
     */
    private String generateId(String name, String email, String phone) {
        String input = name + email + phone;

        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] messageDigest = md.digest(input.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : messageDigest) {
                sb.append(String.format("%02x", b));
            }    
            return sb.toString();
        } catch(NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }
}
