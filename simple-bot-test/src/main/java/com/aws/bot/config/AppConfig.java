package com.aws.bot.config;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

public class AppConfig {
    private String dynamodbTableName;
    private String dynamodbAccessKey;
    private String dynamodbSecretKey;
    private String dynamodbRegionName;

    public AppConfig() {
        Config config = ConfigFactory.load();
        dynamodbTableName = config.getString("dynamodb.tablename");
        dynamodbAccessKey = config.getString("dynamodb.access_key");
        dynamodbSecretKey = config.getString("dynamodb.secret_key");
        dynamodbRegionName = config.getString("dynamodb.region_name");
    }

    public String getDynamodbTableName() {
        return dynamodbTableName;
    }

    public String getDynamodbAccessKey() {
        return dynamodbAccessKey;
    }

    public String getDynamodbSecretKey() {
        return dynamodbSecretKey;
    }

    public String getDynamodbRegionName() {
        return dynamodbRegionName;
    }
}
