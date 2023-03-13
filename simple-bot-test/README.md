# Simple AI Bot Test

## Deployment
```
project_name="SimpleAIBotTest"
api_name=$project_name
lambda_role_name="${project_name}_lambda_role"
lambda_policy="${project_name}_policy"
lambda_function_name="${project_name}Lambda"
dynamodb_table_name="contacts"

# Fetch account_id
account_id=$(aws sts get-caller-identity --query 'Account' --output text)

# Create Dynamodb table - Capacity Mode OnDemand
aws dynamodb create-table --table-name $dynamodb_table_name --attribute-definitions AttributeName=id,AttributeType=S --key-schema AttributeName=id,KeyType=HASH --region us-west-2 --provisioned-throughput ReadCapacityUnits=1,WriteCapacityUnits=1

# Create Sample Dynamodb Item
aws dynamodb put-item --table-name $dynamodb_table_name --item '{"id": {"S": "123"}, "name": {"S": "John Doe"},"email": {"S": "john.doe@example.com"},"phone": {"S": "(555) 555-1212"}}'

# Create the execute role for the function
aws iam create-role --role-name $lambda_role_name --assume-role-policy-document file://lambda_execution_role.json

# Create IAM policy
aws iam create-policy --policy-name $lambda_policy  --policy-document file://lambda_policy.json

# Attach the basic Lambda permissions policy to the role
aws iam attach-role-policy --role-name $lambda_role_name --policy-arn arn:aws:iam::$account_id:policy/$lambda_policy

# Get the ARN of the role
ROLE_ARN=arn:aws:iam::$account_id:role/$lambda_role_name

# Create the Lambda function
aws lambda create-function --function-name $lambda_function_name --runtime java11 --handler com.aws.bot.api.APILambda --role $ROLE_ARN --zip-file fileb://target/simple-bot-test-1.0-SNAPSHOT.jar --timeout 900 --memory-size 256

# Update the Lambda function, if the function already exists
aws lambda update-function-code --function-name $lambda_function_name --zip-file fileb://target/simple-bot-test-1.0-SNAPSHOT.jar

# Invoke the Lambda function - Create Contact
payload='{"httpMethod":"POST","path":"/contacts","headers":{"Content-Type":"application/json"},"body": "{\"name\":\"Jane Smith\", \"email\":\"janesmith@example.com\", \"phone\":\"(123) 456-7890\"}"}'
encoded_payload=$(echo -n $payload | base64)
aws lambda invoke --function-name $lambda_function_name --payload "$encoded_payload" /dev/stdout

# Invoke the Lambda function - List Contacts and verify that John doe and Jane Smith exist
payload='{"httpMethod":"GET","path":"/contacts","headers":{"Content-Type":"application/json"}}'
encoded_payload=$(echo -n $payload | base64)
aws lambda invoke --function-name $lambda_function_name --payload "$encoded_payload" /dev/stdout

# Create an empty API Gateway REST API
aws apigateway create-rest-api --name $api_name --region us-west-2 --endpoint-configuration '{"types":["REGIONAL"]}'

# Create the `contacts` resource
api_id=$(aws apigateway get-rest-apis --query "items[?name=='$api_name'].id" --output text)
parent_id=$(aws apigateway get-resources --rest-api-id $api_id --query 'items[?path==`/`].id' --output text)
aws apigateway create-resource --rest-api-id $api_id --path-part contacts --parent-id $parent_id

# Create the `/slackbotevents` resource
aws apigateway create-resource --rest-api-id $api_id --path-part slackbotevents --parent-id $parent_id

# Create the HTTP method of type `ANY` for the resource, with no authorization required - /contacts
resource_id=$(aws apigateway get-resources --rest-api-id $api_id --query 'items[?path==`/contacts`].id' --output text)
aws apigateway put-method --rest-api-id $api_id --resource-id $resource_id --http-method ANY --authorization-type NONE --request-parameters '{}'

# Create the HTTP method of type `ANY` for the resource, with no authorization required - /slackbotevents
resource_sbe_id=$(aws apigateway get-resources --rest-api-id $api_id --query 'items[?path==`/slackbotevents`].id' --output text)
aws apigateway put-method --rest-api-id $api_id --resource-id $resource_sbe_id --http-method ANY --authorization-type NONE --request-parameters '{}'

# Create an integration for the HTTP method, using a Lambda proxy integration type and the specified Lambda function in the us-west-2 region
account_id=$(aws sts get-caller-identity --query 'Account' --output text)
aws apigateway put-integration --rest-api-id $api_id --resource-id $resource_id --http-method ANY --type AWS_PROXY --integration-http-method POST --uri arn:aws:apigateway:us-west-2:lambda:path/2015-03-31/functions/arn:aws:lambda:us-west-2:$account_id:function:$lambda_function_name/invocations

# Create an integration for the HTTP method, using a Lambda proxy integration type and the specified Lambda function in the us-west-2 region - /slackbotevents
aws apigateway put-integration --rest-api-id $api_id --resource-id $resource_sbe_id --http-method ANY --type AWS_PROXY --integration-http-method POST --uri arn:aws:apigateway:us-west-2:lambda:path/2015-03-31/functions/arn:aws:lambda:us-west-2:$account_id:function:$lambda_function_name/invocations

# Setup integration response and method. The integration response is set to return an HTTP status code of 200
aws apigateway put-integration-response --rest-api-id $api_id --resource-id $resource_id --http-method ANY --status-code 200 --response-templates '{"application/json": ""}'
aws apigateway put-method-response --rest-api-id $api_id --resource-id $resource_id --http-method ANY --status-code 200 --response-models '{"application/json": "Empty"}'

# Setup integration response and method. The integration response is set to return an HTTP status code of 200 - /slackbotevents
aws apigateway put-integration-response --rest-api-id $api_id --resource-id $resource_sbe_id --http-method ANY --status-code 200 --response-templates '{"application/json": ""}'
aws apigateway put-method-response --rest-api-id $api_id --resource-id $resource_sbe_id --http-method ANY --status-code 200 --response-models '{"application/json": "Empty"}'

# Create a deployment for the API, with a stage name of test.
aws apigateway create-deployment --rest-api-id $api_id --stage-name test

# Invoke the API - Create Contact
curl -v -X GET "https://$api_id.execute-api.us-west-2.amazonaws.com/test/contacts" -H 'content-type: application/json' -d '{"name":"Alex Johnson", "email":"ajohnson@example.com", "phone":"(987) 654-3210"}'

# Invoke the API - List all contacts, John, Jane and Alex should be present
curl -v -X GET "https://$api_id.execute-api.us-west-2.amazonaws.com/test/contacts" -H 'content-type: application/json'

# TODO: Send Slack request through postman for testing as curl is changing the casing for X-Slack-Request-Timestamp & X-Slack-Signature,
# figure the solution through curl command
```

## Delete the resources
```
aws dynamodb delete-table --table-name $dynamodb_table_name

# Detach IAM policy from IAM role
aws iam detach-role-policy --role-name $lambda_role_name --policy-arn arn:aws:iam::$account_id:policy/$lambda_policy

# Delete IAM policy Role
aws iam delete-policy --policy-arn arn:aws:iam::$account_id:policy/$lambda_policy

# Delete IAM Role
aws iam delete-role --role-name $lambda_role_name

# Delete the function
aws lambda delete-function --function-name $lambda_function_name

aws apigateway delete-rest-api --rest-api-id $api_id
```