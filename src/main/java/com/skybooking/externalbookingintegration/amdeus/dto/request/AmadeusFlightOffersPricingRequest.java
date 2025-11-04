package com.skybooking.externalbookingintegration.amdeus.dto.request;

import com.skybooking.externalbookingintegration.amdeus.dto.common.FlightOfferDto;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class AmadeusFlightOffersPricingRequest implements Serializable {
    private static final long serialVersionUID = 1L;
    private final FlightOffersPricingDataWrapperDto data = new FlightOffersPricingDataWrapperDto();

    @Data
    public static class FlightOffersPricingDataWrapperDto implements Serializable {
        private static final long serialVersionUID = 1L;
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
