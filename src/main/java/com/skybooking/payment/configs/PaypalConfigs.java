package com.skybooking.payment.configs;

import com.paypal.sdk.Environment;
import com.paypal.sdk.PaypalServerSdkClient;
import com.paypal.sdk.authentication.ClientCredentialsAuthModel;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Configuration
public class PaypalConfigs {
    private final Integer TIMEOUT_SECONDS = 30;

    @Value("${paypal.client-id}")
    private String clientId;

    @Value("${paypal.client-secret}")
    private String clientSecret;

    @Value("${paypal.mode}")
    private String mode;

    @Bean
    public Map<String, String> paypalSdkConfig() {
        Map<String, String> configMap = new HashMap<>();
        configMap.put("mode", mode);
        return configMap;
    }

   /* @Bean
    public OAuthTokenCredential oAuthTokenCredential() {
        return new OAuthTokenCredential(clientId, clientSecret, paypalSdkConfig());
    }

    @Bean
    public APIContext apiContext() throws PayPalRESTException {
        APIContext context = new APIContext(oAuthTokenCredential().getAccessToken());
        context.setConfigurationMap(paypalSdkConfig());
        return context;
    }*/

    @Bean
    public PaypalServerSdkClient paypalServerSdkClient() {
        log.info("Initializing PayPal Server SDK Client in {} mode with in-memory token caching", mode);

        Environment environment = getEnvironment();

        PaypalServerSdkClient client = new PaypalServerSdkClient.Builder()
                 .httpClientConfig(configBuilder -> configBuilder
                        .timeout(TIMEOUT_SECONDS))
                .clientCredentialsAuth(getClientCredentialsAuthModel())
                .environment(environment)
                .build();

        log.info("PayPal Server SDK Client initialized successfully");
        return client;
    }

    private ClientCredentialsAuthModel getClientCredentialsAuthModel() {
        log.info("clientId {}, clientSecret {}",clientId,clientSecret);
        return new ClientCredentialsAuthModel.Builder(
                clientId,
                clientSecret
        ).build();
    }

    @NotNull
    private Environment getEnvironment() {
        Environment environment = "live".equalsIgnoreCase(mode)
                ? Environment.PRODUCTION
                : Environment.SANDBOX;
        return environment;
    }
}

