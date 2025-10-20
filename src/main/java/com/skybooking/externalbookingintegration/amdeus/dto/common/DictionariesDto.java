package com.skybooking.externalbookingintegration.amdeus.dto.common;

import lombok.Data;

import java.util.Map;

@Data
public class DictionariesDto {
    private Map<String, LocationInfoDto> locations;
    private Map<String, String> aircraft;
    private Map<String, String> currencies;
    private Map<String, String> carriers;
}
