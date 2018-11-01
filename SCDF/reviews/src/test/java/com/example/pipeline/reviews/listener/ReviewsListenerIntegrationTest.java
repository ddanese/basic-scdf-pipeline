package com.example.pipeline.reviews.listener;

import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.stream.messaging.Processor;
import org.springframework.cloud.stream.test.binder.MessageCollector;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ReviewsListenerIntegrationTest {

    private final String BAKED_INPUT = "{\"address\":\"1650 W NORTHERN LIGHTS BLVD\",\"latitude\":-149.9054948,\"storeName\":\"STARBUCKS - AK - ANCHORAGE  00002\",\"longitude\":61.19533942}";

    @Autowired
    private Processor processor;

    @Autowired
    private MessageCollector messageCollector;

    @Test
    public void handle_givenBaseRecordWithLatLong_returnsBaseRecordWithReviews()
    {
        processor.input().send(new GenericMessage<>(BAKED_INPUT));

        GenericMessage<String> resultPayload = (GenericMessage<String>) messageCollector.forChannel(processor.output()).poll();
        JSONObject resultJson = new JSONObject(resultPayload.getPayload());

        assertThat(resultJson.get("reviews")).isNotNull();
        assertThat(resultJson.get("reviewCount")).isNotNull();
    }

}
