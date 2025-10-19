package com.skybooking.flightsearch.service.impl;

import com.skybooking.flightsearch.dto.AmadeusFlightOfferResponse;
import com.skybooking.flightsearch.service.AmadeusFlightSearchApi;
import com.skybooking.flightsearch.service.FlightSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
public class FlightSearchServiceImpl implements FlightSearchService {

    @Autowired
    private AmadeusFlightSearchApi amadeusFlightSearchApi;

    @Override
    public AmadeusFlightOfferResponse getFlights(String originLocationCode, String destinationLocationCode, LocalDate departureDate, Integer adults, Integer children) {
        return amadeusFlightSearchApi.getFlightSearch(originLocationCode, destinationLocationCode, departureDate, adults, children);
    }
}
