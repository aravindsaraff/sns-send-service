package com.asaraff.sns.service;

import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.model.PublishRequest;
import com.amazonaws.services.sns.model.PublishResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class SNSNotificationSender {

    @Autowired
    private AmazonSNS snsClient;

    /**
     * Publishes a message to Amazon SNS Topic. Note this is synchronous invocation
     * @param message Message to be published and eventually sent out
     * @return String messageId returned from SNS as confirmation
     */
    public String publishUpdatedNotification(String message, String notificationTopic) {
        //publish to an SNS topic
        PublishRequest publishRequest = new PublishRequest(notificationTopic, message);
        PublishResult publishResult = snsClient.publish(publishRequest);

        //print MessageId of message published to SNS topic
        log.info("Published message {} successfully, with AWS-Id - {}", message, publishResult.getMessageId());
        return publishResult.getMessageId();
    }

}
