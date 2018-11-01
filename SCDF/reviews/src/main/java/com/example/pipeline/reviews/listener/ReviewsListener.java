package com.example.pipeline.reviews.listener;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.cloud.stream.messaging.Processor;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.web.client.RestTemplate;

import com.zdatainc.yelp.*;

import java.util.List;

@EnableBinding(Processor.class)
public class ReviewsListener {

    private Processor processor;

    private RestTemplate restTemplate;

    @Autowired
    public ReviewsListener(Processor processor) { this.processor = processor; }

    @StreamListener(Processor.INPUT)
    public void handle(GenericMessage<String> payload)
    {
        JSONObject jsonPayload = new JSONObject(payload.getPayload());
        String apiKey = "EOqwbHuO1Sia_S6ojsW7AuNqs0gqSEaKRqfY6Yqwkr_YDnw-JjmyA70BTb4T7naPxlYoRx2E8Kb7PXIRUIWu_kjCTefmjCsuOe_u1QwCVbowGwUWoVsp1qZuZTbaW3Yx";
        YelpAPI yelpAPI = YelpAPI.newInstance(apiKey);


        YelpSearchRequest ysr = YelpSearchRequest.newBuilder()
                .withCoordinate(Coordinate.of(jsonPayload.getDouble("longitude"),jsonPayload.getDouble("latitude")))
                .withSearchTerm("Starbucks")
                .withRadiusInMeters(100)
                .build();

        List<YelpBusiness> results = yelpAPI.searchForBusinesses(ysr);

        YelpBusinessDetails ybd = null;
        YelpBusiness business = null;
        for (YelpBusiness bus : results) {
            business = bus;
            if (bus.reviewCount > 0) {
                break;
            }
        }
        jsonPayload.put("reviewCount", business.reviewCount);
        jsonPayload.put("reviews", yelpAPI.getReviewsForBusiness(business));

        processor.output().send(new GenericMessage<>(jsonPayload.toString()));

    }

}
