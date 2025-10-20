package com.skybooking.externalbookingintegration.amdeus.dto.common;

import lombok.Data;

import java.util.List;

@Data
public class FlightOfferDto {
    private String type;
    private String id;
    private String source;
    private Boolean instantTicketingRequired;
    private Boolean nonHomogeneous;
    private Boolean oneWay;
    private Boolean isUpsellOffer;
    private String lastTicketingDate;
    private String lastTicketingDateTime;
    private Integer numberOfBookableSeats;
    private List<ItineraryDto> itineraries;
    private PriceDto price;
    private PricingOptionsDto pricingOptions;
    private List<String> validatingAirlineCodes;
    private List<TravelerPricingDto> travelerPricings;
}
