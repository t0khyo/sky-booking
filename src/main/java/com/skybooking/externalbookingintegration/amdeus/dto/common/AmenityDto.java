package com.skybooking.externalbookingintegration.amdeus.dto.common;

import lombok.Data;

@Data
class AmenityDto {
    private String description;
    private Boolean isChargeable;
    private String amenityType;
    private AmenityProviderDto amenityProvider;
}
