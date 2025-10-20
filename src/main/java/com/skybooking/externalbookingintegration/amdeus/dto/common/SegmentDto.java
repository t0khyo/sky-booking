package com.skybooking.externalbookingintegration.amdeus.dto.common;

import lombok.Data;

@Data
public class SegmentDto {
    private LocationDto departure;
    private LocationDto arrival;
    private String carrierCode;
    private String number;
    private AircraftDto aircraft;
    private OperatingDto operating;
    private String duration;
    private String id;
    private Integer numberOfStops;
    private Boolean blacklistedInEU;
}
