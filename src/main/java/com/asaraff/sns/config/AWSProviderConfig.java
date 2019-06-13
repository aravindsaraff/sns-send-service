package com.asaraff.sns.config;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class AWSProviderConfig {

    @Bean
    AWSCredentialsProvider awsInstanceProfileProvider() {
        // This API does not update if there is a credential change, for that see: InstanceProfileCredentialsProvider(boolean refreshCredentialsAsync)
        AWSCredentialsProvider credentialsProvider = DefaultAWSCredentialsProviderChain.getInstance();
        log.info("CredentialsProvider Dump {}", credentialsProvider.getCredentials().getAWSAccessKeyId());
        return credentialsProvider;
    }
}
