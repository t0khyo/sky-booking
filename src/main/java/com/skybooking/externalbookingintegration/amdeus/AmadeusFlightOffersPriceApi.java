package com.skybooking.externalbookingintegration.amdeus;

import com.skybooking.externalbookingintegration.amdeus.dto.request.AmadeusFlightOffersPricingRequest;
import com.skybooking.integration.ApiIntegration;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class AmadeusFlightOffersPriceApi {
    private final AmadeusConfig amadeusConfig;
    private final AmadeusAuthenticateApi amadeusAuthenticateApi;
    private final ApiIntegration apiIntegration;

    private String accessToken;

    public String getOfferPrice(AmadeusFlightOffersPricingRequest flightOffersPricingRequest) {
        log.info("Flight Offers Pricing Request: {}", flightOffersPricingRequest);

        this.accessToken = amadeusAuthenticateApi.getAccessToken();

        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Bearer " + accessToken);

        String flightOffersPricingResponse = apiIntegration.post(
                amadeusConfig.getBaseUrl(),
                amadeusConfig.getEndpoints().getOfferPrice(),
                headers,
                flightOffersPricingRequest,
                String.class
        );

        log.info("Amadeus Offer Price Response: {}", flightOffersPricingResponse);

        return flightOffersPricingResponse;
    }
}
