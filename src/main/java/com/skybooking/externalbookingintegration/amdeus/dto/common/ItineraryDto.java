package com.skybooking.externalbookingintegration.amdeus.dto.common;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class ItineraryDto implements Serializable {
    private static final long serialVersionUID = 1L;
    private String duration;
    private List<SegmentDto> segments;
}
