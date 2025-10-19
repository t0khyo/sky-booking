package com.skybooking.integration;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.Map;

@Component
@Slf4j
public class ApiIntegration {

    private final WebClient.Builder webClientBuilder;

    public ApiIntegration() {
        this.webClientBuilder = WebClient.builder();
    }

    public <T> T get(String baseUrl, String apiUrl,Map<String, String> headers,  Map<String, Object> params, Class<T> responseType) {
        try {
            return webClientBuilder
                    .baseUrl(baseUrl)
                    .build()
                    .get()
                    .uri(uriBuilder -> {
                        uriBuilder.path(apiUrl);
                        params.forEach(uriBuilder::queryParam);
                        return uriBuilder.build();
                    })
                    .headers(httpHeaders -> headers.forEach(httpHeaders::add))
                    .retrieve()
                    .bodyToMono(responseType)
                    .block();
        }
        catch (WebClientResponseException e) {
            log.error("Error response from API: {} - {}", e.getStatusCode(), e.getResponseBodyAsString());
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error calling API: {}", e.getMessage());
            throw e;
        }
    }

    public <RES> RES post(String baseUrl, String apiUrl, Map<String, String> requestBody, Class<RES> responseType) {
        try {
            log.info(requestBody.toString());
            MultiValueMap<String, String> formParams = new LinkedMultiValueMap<>();
            requestBody.forEach(formParams::add);
            return webClientBuilder
                    .baseUrl(baseUrl)
                    .build()
                    .post()
                    .uri(apiUrl)
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .body(BodyInserters.fromFormData(formParams))
                    .retrieve()
                    .bodyToMono(responseType)
                    .block();
        }
        catch (WebClientResponseException e) {
            log.error("Error response from API: {} - {}", e.getStatusCode(), e.getResponseBodyAsString());
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error calling POST API: {}", e.getMessage());
            throw e;
        }
    }
    public <REQ, RES> RES post(String baseUrl, String apiUrl, REQ requestBody, Class<RES> responseType) {
        try {
            log.info(requestBody.toString());
            return webClientBuilder
                    .baseUrl(baseUrl)
                    .build()
                    .post()
                    .uri(apiUrl)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(responseType)
                    .block();
        }
        catch (WebClientResponseException e) {
            log.error("Error response from API: {} - {}", e.getStatusCode(), e.getResponseBodyAsString());
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error calling POST API: {}", e.getMessage());
            throw e;
        }
    }
}
