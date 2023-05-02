package com.epam.funwithflags.service;

import com.epam.funwithflags.model.Route;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import reactor.core.publisher.Flux;

@RequiredArgsConstructor
@Slf4j
public class CachedRouteService implements RouteService {

    private final CachingService cachingService;
    @Override
    public Flux<Route> getRoutes() {
        return Flux.fromIterable(cachingService.getAllRoutes());
    }

    @Scheduled(fixedRateString = "${data.cache.ttl:60000}")
    public void refreshCache() {
        log.debug("Refreshing cached data");
        cachingService.refresh();
    }
}
