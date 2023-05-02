package com.epam.funwithflags.config;

import com.epam.funwithflags.service.CachedRouteService;
import com.epam.funwithflags.service.CachingService;
import com.epam.funwithflags.service.ReactiveRouteService;
import com.epam.funwithflags.service.RouteService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@ConditionalOnProperty(prefix = "data.cache", name = "enabled", havingValue = "true")
@EnableCaching
@EnableScheduling
public class CacheConfig {

    @Bean
    public CachingService cachingService(ReactiveRouteService reactiveRouteService) {
        return new CachingService(reactiveRouteService);
    }
    @Bean
    @Primary
    public RouteService cachedRouteService(CachingService cachingService) {
        return new CachedRouteService(cachingService);
    }

}
