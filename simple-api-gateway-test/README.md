AWS Tutorial: https://docs.aws.amazon.com/apigateway/latest/developerguide/api-gateway-create-api-as-simple-proxy-for-lambda.html

## Deployment

```
# Create the execution role for the function
aws iam create-role --role-name my_apigateway_role --assume-role-policy-document file://lambda_execution_role.json

# Attach the basic Lambda permissions policy to the role
aws iam attach-role-policy --role-name my_apigateway_role --policy-arn arn:aws:iam::aws:policy/service-role/AWSLambdaBasicExecutionRole

# Get the ARN of the role
ROLE_ARN=$(aws iam get-role --role-name my_apigateway_role --query "Role.Arn" --output text)

# Create the function
aws lambda create-function --function-name SimpleApiGatewayTest --runtime java8 --handler com.aws.apigateway.APIGatewayLambdaFunction --role $ROLE_ARN --zip-file fileb://target/simple-api-gateway-test-1.0-SNAPSHOT.jar

# Update the function, if the function already exists
aws lambda update-function-code --function-name SimpleApiGatewayTest --zip-file fileb://target/simple-api-gateway-test-1.0-SNAPSHOT.jar

# Sending test http event to Lambda, if required
{
  "headers": {
    "Content-Type": "application/json",
    "Custom-Header": "custom-value",
    "day": "Thursday"
  },
  "queryStringParameters": {
    "name": "John",
    "city": "Seattle"
  },
  "body": "{\"time\":\"12:00 PM\"}"
}

# Invoke the api
curl -v -X POST   'https://vna76xh3s3.execute-api.us-west-2.amazonaws.com/test/helloworld?name=John&city=Seattle'   -H 'content-tylpe: application/json'   -H 'day: Thursday'   -d '{ "time": "evening" }'
```