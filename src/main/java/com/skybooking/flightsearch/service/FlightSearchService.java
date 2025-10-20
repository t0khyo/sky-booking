package com.skybooking.flightsearch.service;

import com.skybooking.externalbookingintegration.amdeus.dto.request.AmadeusFlightOffersPricingRequest;
import com.skybooking.externalbookingintegration.amdeus.dto.response.AmadeusFlightOfferResponse;

import java.time.LocalDate;

public interface FlightSearchService {
    AmadeusFlightOfferResponse getFlights(String originLocationCode, String destinationLocationCode, LocalDate departureDate, Integer adults, Integer children);

    String getFlightOffersPrice(AmadeusFlightOffersPricingRequest amadeusFlightOffersPricingRequest);
}
