package com.skybooking.flightsearch.controller;

import com.skybooking.externalbookingintegration.amdeus.dto.request.AmadeusFlightOffersPricingRequest;
import com.skybooking.externalbookingintegration.amdeus.dto.response.AmadeusFlightOfferResponse;
import com.skybooking.flightsearch.service.FlightSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping
public class FlightSearchController {

    @Autowired
    private FlightSearchService flightSearchService;

    @GetMapping("/flight-search")
    public AmadeusFlightOfferResponse getFlights(
            @RequestParam("originLocationCode") String originLocationCode,
            @RequestParam("destinationLocationCode") String destinationLocationCode,
            @RequestParam("departureDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate departureDate,
            @RequestParam("adults") Integer adults,
            @RequestParam(value = "children", defaultValue = "0") Integer children) {
        return flightSearchService.getFlights(originLocationCode, destinationLocationCode, departureDate, adults, children);
    }

    @PostMapping("/flight-offers/pricing")
    public String getFlightOffersPrice(
            @RequestBody AmadeusFlightOffersPricingRequest amadeusFlightOffersPricingRequest
    ) {
        return flightSearchService.getFlightOffersPrice(amadeusFlightOffersPricingRequest);
    }
}
