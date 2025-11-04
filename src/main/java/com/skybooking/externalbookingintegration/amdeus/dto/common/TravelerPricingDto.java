package com.skybooking.externalbookingintegration.amdeus.dto.common;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
class TravelerPricingDto implements Serializable {
    private static final long serialVersionUID = 1L;
    private String travelerId;
    private String fareOption;
    private String travelerType;
    private TravelerPriceDto price;
    private List<FareDetailsBySegmentDto> fareDetailsBySegment;
}
