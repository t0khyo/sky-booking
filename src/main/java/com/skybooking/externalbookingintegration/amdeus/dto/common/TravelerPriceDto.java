package com.skybooking.externalbookingintegration.amdeus.dto.common;

import lombok.Data;

import java.io.Serializable;

@Data
class TravelerPriceDto implements Serializable {
    private static final long serialVersionUID = 1L;
    private String currency;
    private String total;
    private String base;
}
