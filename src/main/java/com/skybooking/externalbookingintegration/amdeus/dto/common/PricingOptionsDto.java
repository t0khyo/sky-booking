package com.skybooking.externalbookingintegration.amdeus.dto.common;

import lombok.Data;

import java.util.List;

@Data
class PricingOptionsDto {
    private List<String> fareType;
    private Boolean includedCheckedBagsOnly;
}
