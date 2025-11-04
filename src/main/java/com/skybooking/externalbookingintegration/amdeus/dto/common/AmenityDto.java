package com.skybooking.externalbookingintegration.amdeus.dto.common;

import lombok.Data;

import java.io.Serializable;

@Data
class AmenityDto implements Serializable {
    private static final long serialVersionUID = 1L;
    private String description;
    private Boolean isChargeable;
    private String amenityType;
    private AmenityProviderDto amenityProvider;
}
