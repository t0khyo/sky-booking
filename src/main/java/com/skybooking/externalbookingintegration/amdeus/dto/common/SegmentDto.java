package com.skybooking.externalbookingintegration.amdeus.dto.common;

import lombok.Data;

import java.io.Serializable;

@Data
public class SegmentDto implements Serializable {
    private static final long serialVersionUID = 1L;
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
