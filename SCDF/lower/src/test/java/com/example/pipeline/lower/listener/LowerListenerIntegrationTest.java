package com.example.pipeline.lower.listener;

import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.stream.messaging.Processor;
import org.springframework.cloud.stream.test.binder.MessageCollector;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
public class LowerListenerIntegrationTest {

    private final String BAKED_INPUT = "{\"address\":\"601 WEST STREET_601 WEST 5TH AVENUE_ANCHORAGE\",\"latitude\":-149.8935557,\"storeName\":\"STARBUCKS - AK - ANCHORAGE  00001\",\"longitude\":61.21759217}";

    @Autowired
    private Processor processor;

    @Autowired
    private MessageCollector messageCollector;

    @Test
    public void handle_givenUpperDataInPayload_returnsLowerCaseDataInPayload() {
        processor.input().send(new GenericMessage<>(BAKED_INPUT));

        GenericMessage<String> resultPayload = (GenericMessage<String>) messageCollector.forChannel(processor.output()).poll();
        JSONObject resultJson = new JSONObject(resultPayload.getPayload());

        assertThat(resultJson.get("address")).isEqualTo("601 west street_601 west 5th avenue_anchorage");
        assertThat(resultJson.get("storeName")).isEqualTo("starbucks - ak - anchorage  00001");
    }
}
