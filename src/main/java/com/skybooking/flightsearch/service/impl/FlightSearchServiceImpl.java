package com.skybooking.flightsearch.service.impl;

import com.skybooking.externalbookingintegration.amdeus.AmadeusFlightOffersPriceApi;
import com.skybooking.externalbookingintegration.amdeus.dto.request.AmadeusFlightOffersPricingRequest;
import com.skybooking.externalbookingintegration.amdeus.dto.response.AmadeusFlightOfferResponse;
import com.skybooking.externalbookingintegration.amdeus.AmadeusFlightSearchApi;
import com.skybooking.flightsearch.service.FlightSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class FlightSearchServiceImpl implements FlightSearchService {

    @Autowired
    private AmadeusFlightSearchApi amadeusFlightSearchApi;

    @Autowired
    private AmadeusFlightOffersPriceApi amadeusFlightOffersPriceApi;

    @Override
    public AmadeusFlightOfferResponse getFlights(String originLocationCode, String destinationLocationCode, LocalDate departureDate, Integer adults, Integer children) {
        return amadeusFlightSearchApi.getFlightSearch(originLocationCode, destinationLocationCode, departureDate, adults, children);
    }

    @Override
    public String getFlightOffersPrice(AmadeusFlightOffersPricingRequest amadeusFlightOffersPricingRequest) {
        return amadeusFlightOffersPriceApi.getOfferPrice(amadeusFlightOffersPricingRequest);
    }
}
