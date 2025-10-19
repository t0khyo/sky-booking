package com.skybooking.flightsearch.service;

import com.skybooking.flightsearch.dto.AmadeusFlightOfferResponse;
import com.skybooking.flightsearch.dto.AmadeusOAuth2TokenResponse;
import com.skybooking.integration.ApiIntegration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Component
public class AmadeusFlightSearchApi {

    private String baseUrl = "https://test.api.amadeus.com";
    private String clientId = "VLc0ON0rBnBDIl46RCytIIvHZTgGVPGo";
    private String clientSecret = "7s6KNXDAgUAoGpzw";

    private String accessToken;
    private int expiresIn;
    private LocalDateTime lastTokenTime;

    @Autowired
    private ApiIntegration flightApiIntegration;

    public AmadeusFlightOfferResponse getFlightSearch(String originLocationCode, String destinationLocationCode, LocalDate departureDate, Integer adults, Integer children) {
        if(isExpiredToken()) {
            refreshAccessToken();
        }
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/x-www-form-urlencoded");
        headers.put("Authorization", "Bearer " + accessToken);

        Map<String, Object> params = new HashMap<>();
        params.put("originLocationCode", originLocationCode);
        params.put("destinationLocationCode", destinationLocationCode);
        params.put("departureDate", departureDate);
        params.put("adults", adults);
        params.put("children", children);
        return flightApiIntegration.get(baseUrl, "/v2/shopping/flight-offers", headers, params, AmadeusFlightOfferResponse.class);
    }

    private void refreshAccessToken() {
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("grant_type", "client_credentials");
        requestBody.put("client_id", clientId);
        requestBody.put("client_secret", clientSecret);

        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/x-www-form-urlencoded");

        AmadeusOAuth2TokenResponse response = flightApiIntegration.post(baseUrl, "/v1/security/oauth2/token", requestBody, AmadeusOAuth2TokenResponse.class);

        this.accessToken = response.getAccessToken();
        this.expiresIn = response.getExpiresIn();
        this.lastTokenTime = LocalDateTime.now();
    }

    private boolean isExpiredToken() {
        if(lastTokenTime == null)
            return true;
        long seconds = Duration.between(lastTokenTime, LocalDateTime.now()).getSeconds();
        return seconds >= expiresIn;
    }
}
