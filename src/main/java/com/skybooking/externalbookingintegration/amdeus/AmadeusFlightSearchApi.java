package com.skybooking.externalbookingintegration.amdeus;

import com.skybooking.externalbookingintegration.amdeus.dto.request.AmadeusFlightSearchRequest;
import com.skybooking.externalbookingintegration.amdeus.dto.response.AmadeusFlightOfferResponse;
import com.skybooking.integration.ApiIntegration;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class AmadeusFlightSearchApi {

    // todo add other api and hardcoded strings to config
    private final AmadeusConfig amadeusConfig;
    private final AmadeusAuthenticateApi amadeusAuthenticateApi;

    private String accessToken;

    @Autowired
    private ApiIntegration flightApiIntegration;

    private void logConfig() {
        log.info("Amadeus Config: BaseUrl={}, ClientId={}, Endpoints={}",
                amadeusConfig.getBaseUrl(),
                amadeusConfig.getClientId(),
                amadeusConfig.getEndpoints());
    }

    @Cacheable(value = "amadeusFlightSearchResponse", keyGenerator = "flightSearchKeyGenerator")
    public AmadeusFlightOfferResponse getFlightSearch(AmadeusFlightSearchRequest flightSearchRequest) {
        logConfig();

        this.accessToken = amadeusAuthenticateApi.getAccessToken();

        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/x-www-form-urlencoded");
        headers.put("Authorization", "Bearer " + accessToken);

        Map<String, Object> params = new HashMap<>();
        params.put("originLocationCode", flightSearchRequest.originLocationCode());
        params.put("destinationLocationCode", flightSearchRequest.destinationLocationCode());
        params.put("departureDate", flightSearchRequest.departureDate());
        params.put("adults", flightSearchRequest.adults());
        params.put("children", flightSearchRequest.children());
        return flightApiIntegration.get(amadeusConfig.getBaseUrl(), amadeusConfig.getEndpoints().getSearchFlights(), headers, params, AmadeusFlightOfferResponse.class);
    }
}
