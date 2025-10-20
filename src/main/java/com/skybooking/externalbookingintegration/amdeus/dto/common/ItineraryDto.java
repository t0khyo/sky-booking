package com.skybooking.externalbookingintegration.amdeus.dto.common;

import lombok.Data;

import java.util.List;

@Data
public class ItineraryDto {
    private String duration;
    private List<SegmentDto> segments;
}
