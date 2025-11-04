package com.skybooking.externalbookingintegration.config;

import com.skybooking.externalbookingintegration.amdeus.dto.request.AmadeusFlightSearchRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.cache.RedisCacheManagerBuilderCustomizer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.time.format.DateTimeFormatter;
import java.util.HexFormat;

@Slf4j
@Configuration
@EnableCaching
public class CacheConfig {
    public static final String FLIGHT_SEARCH_AMADEUS = "skybooking:flight:search:v1:amadeus:";

    @Bean
    public RedisCacheConfiguration cacheConfiguration() {
        return RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofMinutes(60)).serializeValuesWith(
                RedisSerializationContext.SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer()));
    }

    @Bean
    public RedisCacheManagerBuilderCustomizer redisCacheManagerBuilderCustomizer() {
        return (builder) -> builder
                .withCacheConfiguration("amadeusFlightSearchResponse",
                        RedisCacheConfiguration.defaultCacheConfig()
                                .entryTtl(Duration.ofMinutes(5))
                                .disableKeyPrefix()
                );
    }

    @Bean
    public KeyGenerator flightSearchKeyGenerator() {
        return (target, method, params) -> {
            if (params.length == 0) {
                return "empty";
            }

            Object param = params[0];

            if (param instanceof AmadeusFlightSearchRequest request) {
                String searchRequestString = String.join(
                        "|",
                        request.originLocationCode(),
                        request.destinationLocationCode(),
                        request.departureDate().format(DateTimeFormatter.ISO_DATE),
                        request.adults().toString(),
                        request.children().toString()
                );

                String hashedSearchRequest = hashKey(searchRequestString);

                log.info("Generated cache key for AmadeusFlightSearchRequest: {}", FLIGHT_SEARCH_AMADEUS + hashedSearchRequest);

                return FLIGHT_SEARCH_AMADEUS + hashedSearchRequest;
            }

            // Fallback for unexpected parameter types
            return param.toString();
        };
    }

    private static String hashKey(String key) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(key.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not available", e);
        }
    }
}
