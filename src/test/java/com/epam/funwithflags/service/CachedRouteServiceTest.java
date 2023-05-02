package com.epam.funwithflags.service;

import com.epam.funwithflags.model.Route;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.stream.IntStream;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CachedRouteServiceTest {

    @Mock
    CachingService cachingService;
    @InjectMocks
    CachedRouteService cachedRouteService;

    @Test
    void getRoutes() {
        when(cachingService.getAllRoutes()).thenReturn(IntStream.range(0, 10)
                .mapToObj(i -> new Route()).toList());

        Flux<Route> routes = cachedRouteService.getRoutes();

        verify(cachingService).getAllRoutes();

        StepVerifier.create(routes).expectNextCount(10)
                .verifyComplete();
    }

    @Test
    void refreshCache() {
        cachedRouteService.refreshCache();

        verify(cachingService).refresh();
    }
}