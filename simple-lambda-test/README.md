# Simple Lambda Test

The project is a simple example of how to use AWS Lambda service to run Java code. It showcases the steps involved in deploying a Java-based Lambda function to AWS, testing it from the terminal, and granting access to the necessary resources through the AWS cli.

### Prerequisites

* This project was built and deployed through Github Codespaces, so Java must be installed on the Codespaces. 
* The project was built using Maven. To generate the Maven project, use the following command: mvn archetype:generate -DgroupId=com.aws.lambda -DartifactId=simple-lambda-test -DinteractiveMode=false

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
        "Sid": "LambdaFullAccess",
        "Effect": "Allow",
        "Action": [
            "lambda:*"
        ],
        "Resource": "arn:aws:lambda:*:*:*"
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
10.  The configuration is stored in `~/.aws/configure`. The AWS CLI is now configured and ready to use.

## Built With

* To build the project, use the command `mvn clean package`, which compiles the code, runs any tests, and packages the code and any dependencies into a JAR file that can be deployed to AWS Lambda.

## Deployment

To deploy the project, follow these steps:
1. Go to the AWS Lambda console and create a new function using the "Author from Scratch" template.
2. Give the function a name of "simpleLambdaTest" and select Java 8 as the runtime.
3. Once the function is created, go to the function's settings and update the Handler to `com.aws.lambda.LambdaFunction` under Runtime settings.
4. In the terminal, run the command `aws lambda update-function-code --function-name simpleLambdaTest --zip-file fileb://target/simple-lambda-test-1.0-SNAPSHOT.jar` to upload the code to AWS Lambda.
5. Finally, invoke the function using the command `aws lambda invoke --function-name simpleLambdaTest output.txt`.