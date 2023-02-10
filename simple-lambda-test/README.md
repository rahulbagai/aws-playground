# Simple Lambda Test

The project is a simple example of how to use AWS Lambda service to run Java code. It showcases the steps involved in deploying a Java-based Lambda function to AWS, testing it from the terminal, and granting access to the necessary resources through the AWS cli.

### Prerequisites

This project was built and deployed through Github Codespaces, so Java must be installed on the Codespaces. 

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
1. Go to the AWS IAM Users console and click on the "Add user" button.
2. Give the new user a name and keep all other settings as is.
3. Go inside the user and under Permissions, click "Add inline policy" from the dropdown.
4. In the JSON editor, create a policy with the following content, giving it a name of "lambda-access-all":

```{
"Version": "2012-10-17",
"Statement": [
    {
        "Sid": "LambdaFullAccess",
        "Effect": "Allow",
        "Action": [
            "lambda:*"
        ],
        "Resource": "arn:aws:lambda:*:*:*"
    }
    ]
}```

## Built With

* The project was built using Maven. To generate the Maven project, use the following command: 
`mvn archetype:generate -DgroupId=com.aws.lambda -DartifactId=simple-lambda-test -DinteractiveMode=false`
* To build the project, use the command `mvn clean package`, which compiles the code, runs any tests, and packages the code and any dependencies into a JAR file that can be deployed to AWS Lambda.

## Deployment

To deploy the project, follow these steps:
1. Go to the AWS Lambda console and create a new function using the "Author from Scratch" template.
2. Give the function a name of "simpleLambdaTest" and select Java 8 as the runtime.
3. Once the function is created, go to the function's settings and update the Handler to `com.aws.lambda.LambdaFunction`.
4. In the terminal, run the command `aws lambda update-function-code --function-name simpleLambdaTest --zip-file fileb://target/simple-lambda-test-1.0-SNAPSHOT.jar` to upload the code to AWS Lambda.
5. Finally, invoke the function using the command `aws lambda invoke --function-name simpleLambdaTest output.txt`.