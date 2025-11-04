package com.skybooking.externalbookingintegration.amdeus.dto.common;

import lombok.Data;

import java.io.Serializable;

@Data
class FeeDto implements Serializable {
    private static final long serialVersionUID = 1L;
    private String amount;
    private String type;
}
