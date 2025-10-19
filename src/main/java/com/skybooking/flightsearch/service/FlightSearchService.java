package com.skybooking.flightsearch.service;

import com.skybooking.flightsearch.dto.AmadeusFlightOfferResponse;

import java.time.LocalDate;
import java.time.LocalDateTime;

public interface FlightSearchService {
    AmadeusFlightOfferResponse getFlights(String originLocationCode, String destinationLocationCode, LocalDate departureDate, Integer adults, Integer children);
}
