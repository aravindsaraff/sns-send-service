# AWS SNS Send Service #

## Purpose ##

This is a Java 8+ Spring Boot which can be used to send message AWS SNS. The topic is to be given in the REST API call


## Artifacts ##

* sns-send-service

### Running sns-service ###

To Run:
java -jar -Dspring.config.location=<config-location> -Daws.accessKeyId=? -Daws.secretKey=? sns-send-service.jar


* Provide AWS credentials (static credentials sample as above)
    Info: https://docs.aws.amazon.com/sdk-for-java/v1/developer-guide/credentials.html

## Changelog ##    
* 1.0+
    * Add support for API
    * Input payload any json
    * Will post to Topic in AWS
    * Returns 200 AND AWS-id if successful (example: AWS-Id - 0e29af46-dbd0-5678-8a40-fc71a33d9d42)
    * Example payload
    ```
    curl -X POST --header 'Content-Type: application/json' --header 'Accept: text/plain' -d '{ 
     "key":value
     ....
     }' 'http://localhost:8080/v1/sns/publish'
   ```
