package com.skybooking.flightsearch.controller;

import com.skybooking.flightsearch.dto.AmadeusFlightOfferResponse;
import com.skybooking.flightsearch.service.FlightSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/flight-search")
public class FlightSearchController {

    @Autowired
    private FlightSearchService flightSearchService;

    @GetMapping
    public AmadeusFlightOfferResponse getFlights(
            @RequestParam("originLocationCode") String originLocationCode,
            @RequestParam("destinationLocationCode") String destinationLocationCode,
            @RequestParam("departureDate")  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate departureDate,
            @RequestParam("adults") Integer adults,
            @RequestParam(value = "children", defaultValue = "0") Integer children) {
          return flightSearchService.getFlights(originLocationCode, destinationLocationCode, departureDate, adults, children);
    }
}
