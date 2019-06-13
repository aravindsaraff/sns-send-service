package com.asaraff.sns.config;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.AmazonSNSAsync;
import com.amazonaws.services.sns.AmazonSNSAsyncClient;
import com.amazonaws.services.sns.AmazonSNSClient;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import org.dozer.DozerBeanMapperBuilder;
import org.dozer.Mapper;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import javax.annotation.PostConstruct;
import java.util.concurrent.*;

@Configuration
public class AppConfig extends WebMvcConfigurerAdapter {

    @Autowired
    private AWSCredentialsProvider awsConfigProvider;

    @Value("${aws.sns.region}")
    private String region;

    @Bean
    @Primary
    public AmazonSNS amazonSNSClient() {
        return AmazonSNSClient.builder()
                .withCredentials(awsConfigProvider)
                .withRegion(Regions.fromName(region))
                .build();

    }

    /**
     * Either use Sync Above or Async below..
     * @param executorService App Executor service defined below
     * @return AmazonSNSAsync instance
     */
    @Bean(name = "asyncSNSClient")
    public AmazonSNSAsync amazonSNSAsyncClient(@Autowired ExecutorService executorService) {

        return AmazonSNSAsyncClient.asyncBuilder()
                .withCredentials(awsConfigProvider)
                .withRegion(region)
                .withExecutorFactory(() -> executorService)
                .build();
    }

    /**
     * JSON Serialization
     * @return
     */
    @Bean
    public ObjectMapper objectMapper() {

        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JodaModule());
        // can turn this one as well to print dates formatted instead of timestamps
        //mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        mapper.enable(SerializationFeature.INDENT_OUTPUT);

        return mapper;
    }

    @Bean
    public Mapper dozerMapper() {

        return DozerBeanMapperBuilder.create()
                .withMappingFiles("dozerBeanMapping.xml")
                .build();
    }

    /**
     * Cached version of DateTime that uses UTC time zone when constructing DateTime
     */
    @PostConstruct
    public void dateTimeZone() {
        DateTimeZone defaultZone = DateTimeZone.UTC;
        DateTimeZone.setDefault(defaultZone);
    }

    /* CORS support */

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurerAdapter() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedHeaders("x-requested-with", "x-amz-sns-message-type", "Origin", "Content-Type", "Accept", "Bearer", "Cache-Control")
                        .allowedMethods("POST", "GET", "OPTIONS", "DELETE", "PATCH");
            }
        };
    }

    @Bean
    public RestTemplate restTemplate() {
        /**
         * This custom response error handler simply suppresses the exceptions thrown from Spring RestTemplate
         * and allows the application to handle errors. - TBD
         */
        return new RestTemplate();
    }

    @Bean
    public DateTimeFormatter dateTimeFormatter(@Value("${dateFormat}") String dateFormat) {
        return DateTimeFormat.forPattern(dateFormat);

    }

    /**
     * Configuration for the ExecutorService used by RxJava2/AwsAsync asynchronous streams code.
     *
     * @param minThreads Minimum thread pool size
     * @param maxThreads Maximum thread pool size
     * @param threadIdleTimeoutSeconds The timeout period for idle theads over the minimum pool limit. If zero is specified, threads will
     *                                 never be destroyed once they are created.
     * @param maxQueueSize Maximum queue size
     * @return ExecutorService
     */
    @Bean
    public ExecutorService executorService(@Value("${executorservice.threads.minSize}") Integer minThreads,
                                           @Value("${executorservice.threads.maxSize}") Integer maxThreads,
                                           @Value("${executorservice.threads.idleTimeSeconds}") Integer threadIdleTimeoutSeconds,
                                           @Value("${executorservice.queue.maxSize}") Integer maxQueueSize) {
        BlockingQueue<Runnable> blockingQueue = new ArrayBlockingQueue<>(maxQueueSize);
        return new ThreadPoolExecutor(
                minThreads,
                maxThreads,
                threadIdleTimeoutSeconds,
                TimeUnit.SECONDS,
                blockingQueue);
    }

}
