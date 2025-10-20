package com.skybooking.externalbookingintegration.amdeus.dto.common;

import lombok.Data;

import java.util.List;

@Data
class TravelerPricingDto {
    private String travelerId;
    private String fareOption;
    private String travelerType;
    private TravelerPriceDto price;
    private List<FareDetailsBySegmentDto> fareDetailsBySegment;
}
