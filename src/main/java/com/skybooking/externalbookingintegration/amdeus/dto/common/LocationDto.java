package com.skybooking.externalbookingintegration.amdeus.dto.common;

import lombok.Data;

@Data
class LocationDto {
    private String iataCode;
    private String terminal;
    private String at;
}
