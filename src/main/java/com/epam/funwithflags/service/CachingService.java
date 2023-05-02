package com.epam.funwithflags.service;

import com.epam.funwithflags.model.Route;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;

import java.util.List;
import java.util.Objects;

@RequiredArgsConstructor
@Slf4j
public class CachingService {
    private final ReactiveRouteService reactiveRouteService;

    @Cacheable("routes")
    public List<Route> getAllRoutes() {
        log.debug("Routes not found in cache, getting from providers");
        return refresh();
    }

    @CachePut("routes")
    public List<Route> refresh() {
        List<Route> routes = reactiveRouteService.getRoutes().collectList().block();
        log.debug("Retrieved {} routes", Objects.requireNonNull(routes).size());
        return routes;
    }
}
