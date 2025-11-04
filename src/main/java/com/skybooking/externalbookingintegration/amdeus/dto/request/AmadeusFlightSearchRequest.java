package com.skybooking.externalbookingintegration.amdeus.dto.request;

import java.time.LocalDate;

public record AmadeusFlightSearchRequest(
        String originLocationCode,
        String destinationLocationCode,
        LocalDate departureDate,
        Integer adults,
        Integer children
) {
}
