package com.skybooking.externalbookingintegration.amdeus.dto.response;

import com.skybooking.externalbookingintegration.amdeus.dto.common.DictionariesDto;
import com.skybooking.externalbookingintegration.amdeus.dto.common.FlightOfferDto;
import com.skybooking.externalbookingintegration.amdeus.dto.common.MetaDto;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class AmadeusFlightOfferResponse implements Serializable {
    private static final long serialVersionUID = 1L;
    private MetaDto meta;
    private List<FlightOfferDto> data;
    private DictionariesDto dictionaries;
}
