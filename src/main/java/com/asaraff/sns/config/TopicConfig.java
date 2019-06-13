package com.asaraff.sns.config;

import com.asaraff.sns.model.Topic;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Configuration
@ConfigurationProperties(prefix="aws.sns.topic-list")
@Data
@NoArgsConstructor
public class TopicConfig {
    private List<Topic> topics;

    public Map<String,Topic> toMap() {
        return topics.stream().collect(Collectors.toMap(Topic::getName, Function.identity()));
    }
}
