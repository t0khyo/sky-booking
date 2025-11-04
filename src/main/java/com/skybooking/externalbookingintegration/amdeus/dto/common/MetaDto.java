package com.skybooking.externalbookingintegration.amdeus.dto.common;

import lombok.Data;

import java.io.Serializable;

@Data
public class MetaDto implements Serializable {
    private static final long serialVersionUID = 1L;
    private Integer count;
    private LinksDto links;
}
