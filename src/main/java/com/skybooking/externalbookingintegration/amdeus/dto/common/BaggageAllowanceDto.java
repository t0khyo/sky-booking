package com.skybooking.externalbookingintegration.amdeus.dto.common;

import lombok.Data;

import java.io.Serializable;

@Data
class BaggageAllowanceDto implements Serializable {
    private static final long serialVersionUID = 1L;
    private Integer quantity;
    private Integer weight;
    private String weightUnit;
}
