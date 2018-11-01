package com.example.pipeline.lower.listener;

import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.cloud.stream.messaging.Processor;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.GenericMessage;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class LowerListenerTest {

    private LowerListener subject;

    private final String BAKED_INPUT = "{\"address\":\"601 WEST STREET_601 WEST 5TH AVENUE_ANCHORAGE\",\"latitude\":-149.8935557,\"storeName\":\"STARBUCKS - AK - ANCHORAGE  00001\",\"longitude\":61.21759217}";

    @Mock
    private Processor processor;

    @Mock
    private MessageChannel messageChannel;

    @Captor
    private ArgumentCaptor<GenericMessage<String>> processOutputCaptor;

    @Test
    public void handle() {
        given(processor.output()).willReturn(messageChannel);

        GenericMessage<String> bakedMessage = new GenericMessage<>(BAKED_INPUT);

        subject = new LowerListener(processor);

        subject.handle(bakedMessage);

        verify(messageChannel).send(processOutputCaptor.capture());

        JSONObject resultJsonObject = new JSONObject(processOutputCaptor.getValue().getPayload());
        assertThat(resultJsonObject.get("address")).isEqualTo("601 west street_601 west 5th avenue_anchorage");
        assertThat(resultJsonObject.get("storeName")).isEqualTo("starbucks - ak - anchorage  00001");
    }
}