package com.skybooking.flightsearch.dto;

import lombok.Data;
import java.util.List;
import java.util.Map;

@Data
public class AmadeusFlightOfferResponse {
    private Meta meta;
    private List<FlightOffer> data;
    private Dictionaries dictionaries;
}

// Meta Information
@Data
class Meta {
    private Integer count;
    private Links links;
}

@Data
class Links {
    private String self;
}

// Flight Offer
@Data
class FlightOffer {
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
    private List<Itinerary> itineraries;
    private Price price;
    private PricingOptions pricingOptions;
    private List<String> validatingAirlineCodes;
    private List<TravelerPricing> travelerPricings;
}

// Itinerary
@Data
class Itinerary {
    private String duration;
    private List<Segment> segments;
}

// Segment
@Data
class Segment {
    private Location departure;
    private Location arrival;
    private String carrierCode;
    private String number;
    private Aircraft aircraft;
    private Operating operating;
    private String duration;
    private String id;
    private Integer numberOfStops;
    private Boolean blacklistedInEU;
}

@Data
class Location {
    private String iataCode;
    private String terminal;
    private String at;
}

@Data
class Aircraft {
    private String code;
}

@Data
class Operating {
    private String carrierCode;
    private String carrierName;
}

// Price
@Data
class Price {
    private String currency;
    private String total;
    private String base;
    private List<Fee> fees;
    private String grandTotal;
    private List<AdditionalService> additionalServices;
}

@Data
class Fee {
    private String amount;
    private String type;
}

@Data
class AdditionalService {
    private String amount;
    private String type;
}

// Pricing Options
@Data
class PricingOptions {
    private List<String> fareType;
    private Boolean includedCheckedBagsOnly;
}

// Traveler Pricing
@Data
class TravelerPricing {
    private String travelerId;
    private String fareOption;
    private String travelerType;
    private TravelerPrice price;
    private List<FareDetailsBySegment> fareDetailsBySegment;
}

@Data
class TravelerPrice {
    private String currency;
    private String total;
    private String base;
}

@Data
class FareDetailsBySegment {
    private String segmentId;
    private String cabin;
    private String fareBasis;
    private String brandedFare;
    private String brandedFareLabel;
    private String classType; // mapped from "class"
    private BaggageAllowance includedCheckedBags;
    private BaggageAllowance includedCabinBags;
    private List<Amenity> amenities;
}

@Data
class BaggageAllowance {
    private Integer quantity;
    private Integer weight;
    private String weightUnit;
}

@Data
class Amenity {
    private String description;
    private Boolean isChargeable;
    private String amenityType;
    private AmenityProvider amenityProvider;
}

@Data
class AmenityProvider {
    private String name;
}

// Dictionaries
@Data
class Dictionaries {
    private Map<String, LocationInfo> locations;
    private Map<String, String> aircraft;
    private Map<String, String> currencies;
    private Map<String, String> carriers;
}

@Data
class LocationInfo {
    private String cityCode;
    private String countryCode;
}