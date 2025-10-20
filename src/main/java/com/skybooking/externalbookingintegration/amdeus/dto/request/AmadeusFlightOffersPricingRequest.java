package com.skybooking.externalbookingintegration.amdeus.dto.request;

import com.skybooking.externalbookingintegration.amdeus.dto.common.FlightOfferDto;
import lombok.Data;

import java.util.List;

@Data
public class AmadeusFlightOffersPricingRequest {
    private final FlightOffersPricingDataWrapperDto data = new FlightOffersPricingDataWrapperDto();

    @Data
    public static class FlightOffersPricingDataWrapperDto {
        private final String type = "flight-offers-pricing";
        private List<FlightOfferDto> flightOffers;
    }

    public List<FlightOfferDto> getFlightOffers() {
        return data.getFlightOffers();
    }

    public String getType() {
        return data.getType();
    }
}
