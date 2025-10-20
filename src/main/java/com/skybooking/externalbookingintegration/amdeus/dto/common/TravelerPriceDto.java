package com.skybooking.externalbookingintegration.amdeus.dto.common;

import lombok.Data;

@Data
class TravelerPriceDto {
    private String currency;
    private String total;
    private String base;
}
