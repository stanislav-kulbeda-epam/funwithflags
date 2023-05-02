package com.epam.funwithflags.controller;

import com.epam.funwithflags.model.Route;
import com.epam.funwithflags.service.RouteService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;

import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RouteControllerTest {

    public static final int RESULT_SIZE = 10;
    @Mock
    RouteService routeService;
    @InjectMocks
    RouteController controller;

    @BeforeEach
    void setMockOutput() {
        when(routeService.getRoutes()).thenReturn(Flux.fromArray(IntStream.range(0, RESULT_SIZE)
                .mapToObj(i -> new Route()).toArray(Route[]::new)));
    }

    @Test
    void shouldReturnOnePage() {
        int requestedSize = 5;
        Flux<Route> routes = controller.getRoutes(0, requestedSize, Collections.emptyList());

        verify(routeService).getRoutes();

        List<Route> actual = routes.collectList().block();
        assertNotNull(actual);
        assertEquals(requestedSize, actual.size());
    }

    @Test
    void shouldReturnAllRemainingRoutesOnTheLastPage() {
        int requestedSize = 3;
        Flux<Route> routes = controller.getRoutes(3, requestedSize, Collections.emptyList());

        verify(routeService).getRoutes();

        List<Route> actual = routes.collectList().block();
        assertNotNull(actual);
        assertEquals(1, actual.size());
    }
    @Test
    void shouldReturnAllRoutes() {
        int requestedSize = RESULT_SIZE * 2;
        Flux<Route> routes = controller.getRoutes(0, requestedSize, Collections.emptyList());

        verify(routeService).getRoutes();

        List<Route> actual = routes.collectList().block();
        assertNotNull(actual);
        assertEquals(RESULT_SIZE, actual.size());
    }
}