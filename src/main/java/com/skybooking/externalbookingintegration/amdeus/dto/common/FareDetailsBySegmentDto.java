package com.skybooking.externalbookingintegration.amdeus.dto.common;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
class FareDetailsBySegmentDto implements Serializable {
    private static final long serialVersionUID = 1L;
    private String segmentId;
    private String cabin;
    private String fareBasis;
    private String brandedFare;
    private String brandedFareLabel;
    private String classType;
    private BaggageAllowanceDto includedCheckedBags;
    private BaggageAllowanceDto includedCabinBags;
    private List<AmenityDto> amenities;
}
