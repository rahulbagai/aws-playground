package com.aws.bot.config;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

public class AppConfig {

    private String dynamodbTableName;
    private String dynamodbAccessKey;
    private String dynamodbSecreetKey;
    private String dynamodbRegionName;
    private String slackSigningSecret;
    private String slackToken;
    private boolean isOffline;

    public AppConfig() {
        Config config = ConfigFactory.load();
        dynamodbTableName = config.getString("dynamodb.tablename");
        dynamodbAccessKey = config.getString("dynamodb.accesskey");
        dynamodbSecreetKey = config.getString("dynamodb.secretkey");
        dynamodbRegionName = config.getString("dynamodb.regionname");
        slackSigningSecret = config.getString("slack.signingsecret");
        slackToken = config.getString("slack.token");
        isOffline = config.getBoolean("is_offline");
    }

    public String getSlackSigningSecret() {
        return slackSigningSecret;
    }

    public String getSlackToken() {
        return slackToken;
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

    public boolean getIsOffline() {
        return isOffline;
    }
}
