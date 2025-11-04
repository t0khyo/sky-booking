package com.skybooking.externalbookingintegration.amdeus.dto.common;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
class PriceDto implements Serializable {
    private static final long serialVersionUID = 1L;
    private String currency;
    private String total;
    private String base;
    private List<FeeDto> fee;
    private String grandTotal;
    private List<AdditionalServiceDto> additionalService;
}
