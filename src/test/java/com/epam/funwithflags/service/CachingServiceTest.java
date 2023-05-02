package com.epam.funwithflags.service;

import com.epam.funwithflags.model.Route;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CachingServiceTest {

    @Mock
    ReactiveRouteService routeService;
    @InjectMocks
    CachingService cachingService;

    @BeforeEach
    void setMockOutput() {
        when(routeService.getRoutes()).thenReturn(Flux.fromArray(IntStream.range(0, 10)
                .mapToObj(i -> new Route()).toArray(Route[]::new)));
    }
    @Test
    void shouldGetAllRoutes() {
        List<Route> allRoutes = cachingService.getAllRoutes();

        verify(routeService).getRoutes();

        assertEquals(10, allRoutes.size());
    }

    @Test
    void shouldRefresh() {
        List<Route> allRoutes = cachingService.refresh();

        verify(routeService).getRoutes();

        assertEquals(10, allRoutes.size());
    }
}