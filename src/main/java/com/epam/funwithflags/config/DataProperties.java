package com.epam.funwithflags.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@ConfigurationProperties(prefix = "data")
@Data
public class DataProperties {
    List<String> providers;
    Cache cache;

    @Data
    public static class Cache {
        boolean enabled;
        long ttl;
    }
}
