package com.aws.bot.config;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

public class AppConfig {

    private String dynamodbTableName;
    private String dynamodbAccessKey;
    private String dynamodbSecreetKey;
    private String dynamodbRegionName;

    public AppConfig() {
        Config config = ConfigFactory.load();
        dynamodbTableName = config.getString("dynamodb.tablename");
        dynamodbAccessKey = config.getString("dynamodb.accesskey");
        dynamodbSecreetKey = config.getString("dynamodb.secretkey");
        dynamodbRegionName = config.getString("dynamodb.regionname");
    }

    public String getDynamodbTableName() {
        return dynamodbTableName;
    }

    public String getDynamodbAccessKey() {
        return dynamodbAccessKey;
    }

    public String getDynamodbSecreetKey() {
        return dynamodbSecreetKey;
    }

    public String getDynamodbRegionName() {
        return dynamodbRegionName;
    }
}
