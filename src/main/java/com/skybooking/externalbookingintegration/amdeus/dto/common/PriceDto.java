package com.skybooking.externalbookingintegration.amdeus.dto.common;

import lombok.Data;

import java.util.List;

@Data
class PriceDto {
    private String currency;
    private String total;
    private String base;
    private List<FeeDto> fee;
    private String grandTotal;
    private List<AdditionalServiceDto> additionalService;
}
