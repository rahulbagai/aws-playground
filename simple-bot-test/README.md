# Slack Bot with AWS Lambda and Wit.ai

This project provides a basic framework for building a Slack bot using AWS Lambda and Wit.ai. The project uses AWS API Gateway to expose the bot's endpoints, AWS Lambda to handle the business logic, AWS DynamoDB to store data, and Wit.ai to provide natural language processing capabilities.

The bot can receive messages from users in Slack and reply to them using a pre-defined set of responses. The natural language processing capabilities of Wit.ai allow the bot to understand and interpret messages from users, making it easier to provide relevant and useful responses.

This project is an address book application built using AWS API Gateway and AWS Lambda. The application provides basic CRUD functionality, allowing users to create, read, update, and delete contact information (name, email address, phone number). The contact data is stored in an AWS DynamoDB table.

To use the application, users must first deploy the AWS Lambda functions and the API Gateway using the AWS CLI or AWS Console. They also need to set up the necessary credentials and permissions to access the DynamoDB table.

Once set up, users can interact with the application through the API Gateway endpoints, using HTTP requests to create, read, update, and delete contacts. The application uses AWS Lambda functions to handle these requests and interacts with DynamoDB for data storage.

## Prerequisites

* Knowledge of Java, RESTful APIs, AWS API Gateway, AWS Lambda and serverless architecture at an intermediate level.
* Basic Knowledge of using Slack and access to a Slack workspace with admin privileges.
* Ability to use Git to work with the project.

## Installation

* Setting up Slack app and bot user
    * Log in to api.slack.com and click on "Create an app" to create a new app.
    * Give the app a name "AddressBook" and assign it to a workspace. Click "Create App" to proceed.
    * Go to OAuth & Permissions, Scopes and add Bot Token Scopes "chat:write", "app_mentions:read", and then click "Install to Workspace" under OAuth Tokens for Your Workspace and click "Allow" to Perform actions in channels and conversations.
    * Check the workspace using slack.com and click "Launch Slack". You can also use Slack in browser.
* Setting up Slack Bot
    * 

## Usage

* Instructions on how to run the application and interact with the bot on Slack

## Architecture and Technologies

* High-level overview of the architecture and technologies used in the project

