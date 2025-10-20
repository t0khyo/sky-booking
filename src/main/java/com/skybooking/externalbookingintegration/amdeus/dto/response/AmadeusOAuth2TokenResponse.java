package com.skybooking.externalbookingintegration.amdeus.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class AmadeusOAuth2TokenResponse {

    private String type;

    private String username;

    @JsonProperty("application_name")
    private String applicationName;

    @JsonProperty("client_id")
    private String clientId;

    @JsonProperty("token_type")
    private String tokenType;

    @JsonProperty("access_token")
    private String accessToken;

    @JsonProperty("expires_in")
    private Integer expiresIn;

    private String state;

    private String scope;
}