package com.example.pipeline.lower.listener;


import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.cloud.stream.messaging.Processor;
import org.springframework.messaging.support.GenericMessage;

@EnableBinding(Processor.class)
public class LowerListener {

    private Processor processor;

    @Autowired
    public LowerListener(Processor processor) {
        this.processor = processor;
    }

    @StreamListener(Processor.INPUT)
    public void handle(GenericMessage<String> payload)
    {
        JSONObject jsonPayload = new JSONObject(payload.getPayload());
        jsonPayload.put("address", jsonPayload.get("address").toString().toLowerCase());
        jsonPayload.put("storeName", jsonPayload.get("storeName").toString().toLowerCase());

        // return the lowercase string
        processor.output().send(new GenericMessage<>(jsonPayload.toString()));

    }
}
