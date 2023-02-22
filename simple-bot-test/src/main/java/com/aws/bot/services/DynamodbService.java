package com.aws.bot.services;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.ScanRequest;
import software.amazon.awssdk.services.dynamodb.model.ScanResponse;

public class DynamodbService {
    private String accessKey;
    private String secretAccessKey;
    private String regionName;
    private String tableName;
    private DynamoDbClient dynamoDbClient;

    public DynamodbService(Config config) {
        accessKey = config.getString('dynamodb.access_key');
        secretAccessKey = config.getString('dynamodb.secret_access_key');
        regionName = config.getString('dynamodb.region_name');
        tableName = config.getString('dynamodb.table_name')

        AwsBasicCredentials awsCredentials = AwsBasicCredentials.create(accessKey, secretAccessKey);
        StaticCredentialsProvider credentialsProvider = StaticCredentialsProvider.create(awsCredentials);
        Region region = Region.of(regionName);

        dynamoDbClient = DynamoDbClient.builder()
                            .region(region)
                            .credentialsProvider(credentialsProvider)
                            .build();
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
        
        PutItemResponse response = dynamoDbClient.putItem(request);
    }

    public List<Map<String, AttributeValue>> listItems() {
        ScanRequest scanRequest = ScanRequest.builder()
                .tableName("Contacts")
                .build();

        ScanResult scanResult = dynamoDbClient.scan(scanRequest);

        List<Map<String, AttributeValue>> items = new ArrayList<>();
        for (Map<String, AttributeValue> item : scanResult.getItems()) {
            items.add(item);
        }

        return listItems();
    }
}