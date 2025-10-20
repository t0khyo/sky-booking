package com.skybooking.externalbookingintegration.amdeus;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "amadeus")
public class AmadeusConfig {
    private final Client client = new Client();
    private final Api api = new Api();

    public String getBaseUrl() {
        return this.api.getBaseUrl();
    }

    public String getClientId() {
        return this.client.getId();
    }

    public String getClientSecret() {
        return this.client.getSecret();
    }

    public Api.Endpoints getEndpoints() {
        return this.api.getEndpoints();
    }

    @Getter
    @Setter
    public static class Client {
        private String id;
        private String secret;
    }

    @Getter
    @Setter
    public static class Api {
        private String baseUrl;
        private final Endpoints endpoints = new Endpoints();

        @Getter
        @Setter
        @ToString
        public static class Endpoints {
            private String authenticate;
            private String searchFlights;
            private String offerPrice;
            private String createOrder;
        }
    }
}
