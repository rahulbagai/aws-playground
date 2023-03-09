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
aws dynamodb put-item --table-name $dynamodb_table_name --item '{"id": {"S": "123"}, "name": {"S": "John Doe"},"email": {"S": "john.doe@example.com"},"phone": {"S": "+1 (212) 555-1234"}}'

# Create the execute role for the function
aws iam create-role --role-name $lambda_role_name --assume-role-policy-document file://lambda_execution_role.json

# Create IAM policy
aws iam create-policy --policy-name $lambda_policy  --policy-document file://lambda_policy.json

# Attach the basic Lambda permissions policy to the role
aws iam attach-role-policy --role-name $lambda_role_name --policy-arn arn:aws:iam::$account_id:policy/$lambda_policy

# Get the ARN of the role
ROLE_ARN=arn:aws:iam::$account_id:role/$lambda_role_name

# Create the Lambda function
aws lambda create-function --function-name $lambda_function_name --runtime java11 --handler com.aws.bot.api.ListContacts --role $ROLE_ARN --zip-file fileb://target/simple-bot-test-1.0-SNAPSHOT.jar

# Update the Lambda function, if the function already exists
aws lambda update-function-code --function-name $lambda_function_name --zip-file fileb://target/simple-bot-test-1.0-SNAPSHOT.jar

# Invoke the Lambda function
payload='{"headers":{"Content-Type":"application/json"}}'
encoded_payload=$(echo -n $payload | base64)
aws lambda invoke --function-name $lambda_function_name --payload "$encoded_payload" output.txt
cat output.txt
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
```