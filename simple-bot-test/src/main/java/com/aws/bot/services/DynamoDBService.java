package com.aws.bot.services;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;

import com.aws.bot.config.AppConfig;
import com.typesafe.config.Config;
import java.util.*;

public class DynamoDBService {
    private DynamoDbClient dynamoDbClient;
    private String tableName;

    public DynamoDBService(AppConfig config) {
        String accessKey = config.getDynamodbAccessKey();
        String secretAccessKey = config.getDynamodbSecretKey();
        String regionName = config.getDynamodbRegionName();
        tableName = config.getDynamodbTableName();

        AwsBasicCredentials awsCredentials = AwsBasicCredentials.create(accessKey, secretAccessKey);
        StaticCredentialsProvider credentialsProvider = StaticCredentialsProvider.create(awsCredentials);
        Region region = Region.of(regionName);

        dynamoDbClient = DynamoDbClient.builder()
                .region(region)
                .credentialsProvider(credentialsProvider)
                .build();
    }

    public DynamoDBService(DynamoDbClient dynamoDbClient) {
        this.dynamoDbClient = dynamoDbClient;
    }

    public void writeItem(String name, String email, String phone) {
        Map<String, AttributeValue> item = new HashMap<>();
        item.put("name", AttributeValue.builder().s(name).build());
        item.put("email", AttributeValue.builder().s(email).build());
        item.put("phone", AttributeValue.builder().s(phone).build());

        PutItemRequest request = PutItemRequest.builder()
                .tableName(tableName)
                .item(item)
                .build();

        dynamoDbClient.putItem(request);
    }

    public List<Map<String, AttributeValue>> listItems() {
        ScanRequest scanRequest = ScanRequest.builder()
                .tableName(tableName)
                .build();

        ScanResponse scanResponse = dynamoDbClient.scan(scanRequest);

        List<Map<String, AttributeValue>> items = new ArrayList<>();
        for (Map<String, AttributeValue> item : scanResponse.items()) {
            items.add(item);
        }

        return items;
    }
}