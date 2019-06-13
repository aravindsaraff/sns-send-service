package com.asaraff.sns.service;

import com.asaraff.sns.config.TopicConfig;
import com.asaraff.sns.model.Topic;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.schedulers.Schedulers;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutorService;

@Component
@Slf4j
public class SnsService {

    @Autowired
    private TopicConfig topicConfig;

    @Autowired
    private ExecutorService executor;

    @Autowired
    private SNSNotificationSender awsSNS;

    @Autowired
    private ObjectMapper jsonMapper;

    private Map<String, Topic> topicConfigMap;

    @PostConstruct
    void getTopicMapForList() {
        topicConfigMap = topicConfig.toMap();
    }


    /**
     * This would invoke the AWS asynchronously using Rx2.
     *
     * @param name - String name of topic to search in topicConfigMap
     * @param request - String representation of payload
     * @return Async/Observable processed Unit
     */
    public Observable<String> publishToTopic(String name, String request) {
        Topic topic = topicConfigMap.get(name);

        if (!Optional.ofNullable(topic).isPresent()) {
            log.error("Error in Publishing to AWS Topic, {} topic not found", name);
            return Observable.just(StringUtils.EMPTY);
        }

        return Observable.create((ObservableOnSubscribe<String>) observer -> {
            try {
                log.debug("Payload {} posting to SNS endpoint {}", request, topic.getNotification());
                String snsPublishId = awsSNS.publishUpdatedNotification(request, topic.getNotification());
                observer.onNext(String.format("AWSId: %s", snsPublishId));
                observer.onComplete();
            } catch (Exception ex) {
                log.error("Error in Publishing to AWS Topic: Return Empty Response", ex);
                observer.onError(ex);
            }

        }).subscribeOn(Schedulers.from(executor));
    }
}
