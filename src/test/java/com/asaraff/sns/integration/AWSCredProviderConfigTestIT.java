package com.asaraff.sns.integration;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.asaraff.sns.config.AWSProviderConfig;
import com.asaraff.sns.config.AppConfig;
import com.asaraff.sns.config.TopicConfig;
import com.asaraff.sns.model.Topic;
import com.asaraff.sns.service.SNSNotificationSender;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
@Import({AppConfig.class, AWSProviderConfig.class, TopicConfig.class})
public class AWSCredProviderConfigTestIT {

    @Autowired
    AWSCredentialsProvider awsConfig;

    @Autowired
    TopicConfig topicConfig;

    @Autowired
    SNSNotificationSender notificationSender;

    @Test
    public void testAWSConfigLoadSuccess() {
        assertThat(awsConfig).isNotNull();
    }

    @Test
    public void testAWSSNSMessageSendSuccess() {
        //TBD
        assertThat("").isNotBlank();
    }
}
