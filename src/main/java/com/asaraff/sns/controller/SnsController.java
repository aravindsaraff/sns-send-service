package com.asaraff.sns.controller;

import com.asaraff.sns.service.SNSNotificationSender;
import com.asaraff.sns.service.SnsService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.DeferredResult;

import javax.validation.constraints.NotNull;

@RestController
@RequestMapping("/v1/messages")
@Api(value = "Sns AWS Service")
@Slf4j
public class SnsController {

    @Value("${default.lookback}")
    private Integer defaultLookbackInMinutes;

    @Value("${spring.async.deferred.timeout}")
    private Long deferredTimeout;

    @Autowired
    private SnsService service;


    @ApiOperation(value = "Post Requests to AWS SNS Asynchronously")
    @RequestMapping(value = "/{topic}/publish", method = RequestMethod.POST)
    @ResponseBody
    public DeferredResult<ResponseEntity<String>> publishMessageRequest(
            @NotNull @PathVariable("topic") String topicName,
            @NotNull @RequestBody String request) {
        final DeferredResult<ResponseEntity<String>> deferredResult
                = new DeferredResult<>(deferredTimeout);

        service.publishToTopic(topicName, request)
                .map(e -> new ResponseEntity<>(
                e,
                addResponseHeaders(), getHttpStatus(e)))
                .subscribe(
                        deferredResult::setResult,
                        deferredResult::setErrorResult
                );

        return deferredResult;

    }

    //--- Private methods
    private static HttpHeaders addResponseHeaders() {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.setContentType(MediaType.APPLICATION_JSON);
        return responseHeaders;
    }

    private HttpStatus getHttpStatus(String object) {
       if(StringUtils.isNotBlank(object)) {
           return HttpStatus.OK;
        } else {
            return HttpStatus.NOT_FOUND;
        }
    }

}
