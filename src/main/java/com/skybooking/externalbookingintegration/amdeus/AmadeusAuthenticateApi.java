package com.skybooking.externalbookingintegration.amdeus;

import com.skybooking.externalbookingintegration.amdeus.dto.response.AmadeusOAuth2TokenResponse;
import com.skybooking.integration.ApiIntegration;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class AmadeusAuthenticateApi {
    private final AmadeusConfig amadeusConfig;
    private final ApiIntegration apiIntegration;

    private String accessToken;
    private int expiresIn;
    private LocalDateTime lastTokenTime;

    public String getAccessToken() {
        lazyRefreshToken();
        return accessToken;
    }

    private void lazyRefreshToken() {
        if (!isTokenExpired()) {
            return;
        }

        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("grant_type", "client_credentials");
        requestBody.put("client_id", amadeusConfig.getClientId());
        requestBody.put("client_secret", amadeusConfig.getClientSecret());

        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/x-www-form-urlencoded");

        AmadeusOAuth2TokenResponse response = apiIntegration.post(amadeusConfig.getBaseUrl(), amadeusConfig.getEndpoints().getAuthenticate(), requestBody, AmadeusOAuth2TokenResponse.class);

        this.accessToken = response.getAccessToken();
        this.expiresIn = response.getExpiresIn();
        this.lastTokenTime = LocalDateTime.now();
    }

    private boolean isTokenExpired() {
        if (lastTokenTime == null) {
            return true;
        }
        LocalDateTime expiryTime = lastTokenTime.plusSeconds(expiresIn - 60); // Refresh 1 minute before expiry
        return LocalDateTime.now().isAfter(expiryTime);
    }

}
