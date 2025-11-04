package com.skybooking.externalbookingintegration.amdeus.dto.common;

import lombok.Data;

import java.io.Serializable;

@Data
class LocationDto implements Serializable {
    private static final long serialVersionUID = 1L;
    private String iataCode;
    private String terminal;
    private String at;
}
