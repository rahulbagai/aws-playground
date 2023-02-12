# Simple Lambda Test

This project provides a hands-on example of utilizing the AWS Lambda service to run Java code. It demonstrates the steps necessary to deploy a Java-based Lambda function, test it, and provide access to necessary resources through the AWS CLI.

### Prerequisites

* This project was built and deployed through Github Codespaces, so Java must be installed on the Codespaces. 
* The project was built using Maven. To generate the Maven project, use the following command: 
`mvn archetype:generate -DgroupId=com.aws.lambda -DartifactId=simple-lambda-test -DinteractiveMode=false`

### Installing

* Install the AWS cli by running the following command (if not already installed) :-
```
curl "https://awscli.amazonaws.com/awscli-exe-linux-x86_64.zip" -o "awscliv2.zip"
unzip awscliv2.zip
sudo ./aws/install
```

* Verify that the AWS CLI has been installed successfully by running the following command :-

```
aws --version
```

* To configure the AWS CLI, follow these steps:
    1. Go to the AWS IAM Users console and click on the "Add users" button.
    2. Give the new user a name and keep all other settings as is, and Create User.
    3. Go inside the user and under Permissions policies, click "Add inline policy" from the dropdown.
    4. In the JSON editor, create a policy with the following content, giving it a name of "lambda-access-all":
    ```
    {
        "Version": "2012-10-17",
        "Statement": [
            {
                "Sid": "FullAccess",
                "Effect": "Allow",
                "Action": [
                    "*"
                ],
                "Resource": "*"
            }
        ]
    }
    ```
    5. Go to the "Security Credentials" section and Under "Access Keys", Click on the "Create Access Key" button.
    6. Use the Command Line Interface (CLI) and move to the Next.
    7. Give a tag value if you want to and press Create access key.
    8. Copy and paste Access Key and Secret Key or Download .csv file.
    9. Open a terminal window, Run the command `aws configure`. Enter the following information when prompted:
    ```
    AWS Access Key ID: *****************
    AWS Secret Access Key: ****************
    Default region name: us-west-2
    Default output format: json
    ```
    10.  The configuration is stored in `~/.aws/credentials`. The AWS CLI is now configured and ready to use.

## Built With

* To build the project `simple-lambda-test`, use the command `mvn clean package` inside simple-lambda-test directory, which compiles the code, runs any tests, and packages the code and any dependencies into a JAR file that can be deployed to AWS Lambda.

## Deployment

```
# Create the execution role for the function
aws iam create-role --role-name my_lambda_role --assume-role-policy-document file://lambda_execution_role.json

# Attach the basic Lambda permissions policy to the role
aws iam attach-role-policy --role-name my_lambda_role --policy-arn arn:aws:iam::aws:policy/service-role/AWSLambdaBasicExecutionRole

# Get the ARN of the role
ROLE_ARN=$(aws iam get-role --role-name my_lambda_role --query "Role.Arn" --output text)

# Create the function
aws lambda create-function --function-name SimpleLambdaTest --runtime java8 --handler com.aws.lambda.LambdaFunction --role $ROLE_ARN --zip-file fileb://target/simple-lambda-test-1.0-SNAPSHOT.jar

# If the function already exist, to update the function
aws lambda update-function-code --function-name SimpleLambdaTest --zip-file fileb://target/simple-lambda-test-1.0-SNAPSHOT.jar

# Invoke the function
aws lambda invoke --function-name SimpleLambdaTest output.txt

# Check the output
cat output.txt
```