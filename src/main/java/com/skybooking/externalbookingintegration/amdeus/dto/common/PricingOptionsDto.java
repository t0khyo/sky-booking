package com.skybooking.externalbookingintegration.amdeus.dto.common;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
class PricingOptionsDto implements Serializable {
    private static final long serialVersionUID = 1L;
    private List<String> fareType;
    private Boolean includedCheckedBagsOnly;
}
