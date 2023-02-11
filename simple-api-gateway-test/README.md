# Simple API Gateway Test
The project is focused on setting up a connection between API Gateway and a Lambda function. This connection allows requests made to the API to be directly sent to the Lambda function for processing without the need for a separate backend infrastructure. The Lambda function then sends the response back to the API Gateway, which is returned to the client.

This setup provides a way to create scalable and reliable APIs that are powered by serverless technology. It eliminates the need to manage a separate backend and allows the focus to be on writing the logic for the API.

The project includes configuring a REST API within API Gateway and creating a Lambda function. The connection between these two components will be established through a proxy integration. This involves defining the routes and methods of the API. The objective of the project is to have a functioning API that can handle incoming requests and provide appropriate responses.

## Getting Started
* In order to deploy this project, it is necessary to install and configure the AWS CLI. This tool is used to manage resources in the AWS environment.
* To obtain a local copy of the project, clone the repository from GitHub using Git. Alternatively, the project can be accessed using GitHub Codespaces and the master branch.
* To use the AWS CLI, an AWS account must be set up and the credentials must be configured within the AWS CLI. This will allow the AWS CLI to interact with the AWS environment and deploy the necessary resources for the project.

## Build
```
mvn clean package
```

## Deployment
```
project_name="SimpleApiGateway"
api_name=$project_name
lambda_role_name="${project_name}_lambda_role"
lambda_function_name="${project_name}Lambda"

# Create the execution role for the function
aws iam create-role --role-name $lambda_role_name --assume-role-policy-document file://lambda_execution_role.json

# Attach the basic Lambda permissions policy to the role
aws iam attach-role-policy --role-name $lambda_role_name --policy-arn arn:aws:iam::aws:policy/service-role/AWSLambdaBasicExecutionRole

# Get the ARN of the role
ROLE_ARN=$(aws iam get-role --role-name $lambda_role_name --query "Role.Arn" --output text)

# Create the Lambda function
aws lambda create-function --function-name $lambda_function_name --runtime java8 --handler com.aws.apigateway.APIGatewayLambdaFunction --role $ROLE_ARN --zip-file fileb://target/simple-api-gateway-test-1.0-SNAPSHOT.jar

# Update the Lambda function, if the function already exists
aws lambda update-function-code --function-name $lambda_function_name --zip-file fileb://target/simple-api-gateway-test-1.0-SNAPSHOT.jar

# Invoke the Lambda function
payload='{"headers":{"Content-Type":"application/json","Custom-Header":"custom-value","day":"Thursday"},"queryStringParameters":{"name":"John","city":"Seattle"},"body":"{\"time\":\"12:00 PM\"}"}'
encoded_payload=$(echo -n $payload | base64)
aws lambda invoke --function-name $lambda_function_name --payload "$encoded_payload" output.txt
cat output.txt

# Create an empty API Gateway REST API
aws apigateway create-rest-api --name $api_name --region us-west-2 --endpoint-configuration '{"types":["REGIONAL"]}'

# Create the `helloworld` resource
api_id=$(aws apigateway get-rest-apis --query "items[?name=='$api_name'].id" --output text)
parent_id=$(aws apigateway get-resources --rest-api-id $api_id --query 'items[?path==`/`].id' --output text)
aws apigateway create-resource --rest-api-id $api_id --path-part helloworld --parent-id $parent_id

# Create the HTTP method of type `ANY` for the resource, with no authorization required
resource_id=$(aws apigateway get-resources --rest-api-id $api_id --query 'items[?path==`/helloworld`].id' --output text)
aws apigateway put-method --rest-api-id $api_id --resource-id $resource_id --http-method ANY --authorization-type NONE --request-parameters '{}'

# Create an integration for the HTTP method, using a Lambda proxy integration type and the specified Lambda function in the us-west-2 region
account_id=$(aws sts get-caller-identity --query 'Account' --output text)
aws apigateway put-integration --rest-api-id $api_id --resource-id $resource_id --http-method ANY --type AWS --integration-http-method POST --uri arn:aws:apigateway:us-west-2:lambda:path/2015-03-31/functions/arn:aws:lambda:us-west-2:$account_id:function:$lambda_function_name/invocations

# Add a permission to the Lambda Function, allowing API Gateway to invoke the function
aws lambda add-permission --function-name $lambda_function_name --action 'lambda:InvokeFunction' --principal apigateway.amazonaws.com --statement-id apigateway-invoke --region us-west-2

# Setup integration response and method. The integration response is set to return an HTTP status code of 200
aws apigateway put-integration-response --rest-api-id $api_id --resource-id $resource_id --http-method ANY --status-code 200 --response-templates '{"application/json": ""}'
aws apigateway put-method-response --rest-api-id $api_id --resource-id $resource_id --http-method ANY --status-code 200 --response-models '{"application/json": "Empty"}'

# Create a deployment for the API, with a stage name of test.
aws apigateway create-deployment --rest-api-id $api_id --stage-name test

# Invoke the API
curl -v -X POST   "https://$api_id.execute-api.us-west-2.amazonaws.com/test/helloworld?name=John&city=Seattle"   -H 'content-tylpe: application/json'   -H 'day: Thursday'   -d '{ "time": "evening" }'
```

## Delete the resources
```
# Detach IAM Policies
policies=$(aws iam list-attached-role-policies --role-name $lambda_role_name --query 'AttachedPolicies[*].PolicyArn' --output text)
for policy in $policies; do
  aws iam detach-role-policy --role-name $lambda_role_name --policy-arn "$policy"
done

# Delete IAM Role
aws iam delete-role --role-name $lambda_role_name

# Delete the function
aws lambda delete-function --function-name $lambda_function_name

# Delete API Gateway
api_id=$(aws apigateway get-rest-apis --query "items[?name=='$api_name'].id" --output text)
aws apigateway delete-rest-api --rest-api-id $api_id
```

## Tutorial
https://docs.aws.amazon.com/apigateway/latest/developerguide/api-gateway-create-api-as-simple-proxy-for-lambda.html