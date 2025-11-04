package com.skybooking.externalbookingintegration.amdeus.dto.common;

import lombok.Data;

import java.io.Serializable;

@Data
class OperatingDto implements Serializable {
    private static final long serialVersionUID = 1L;
    private String carrierCode;
    private String carrierName;
}