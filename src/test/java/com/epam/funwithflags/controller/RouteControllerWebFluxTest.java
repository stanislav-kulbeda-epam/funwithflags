package com.epam.funwithflags.controller;

import com.epam.funwithflags.model.Route;
import com.epam.funwithflags.service.RouteService;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;

import java.util.*;
import java.util.stream.IntStream;

import static com.epam.funwithflags.controller.RouteController.DEFAULT_PAGE_SIZE;
import static org.mockito.BDDMockito.given;

@RunWith(SpringRunner.class)
@WebFluxTest(RouteController.class)
public class RouteControllerWebFluxTest {

    @Autowired
    private WebTestClient webClient;

    @MockBean
    private RouteService routeService;

    @Test
    public void shouldReturnDefaultPage() {
        given(this.routeService.getRoutes())
                .willReturn(Flux.fromArray(IntStream.range(0, 50)
                        .mapToObj(i -> {
                            Route route = new Route();
                            route.setStops(1);
                            return route;
                        })
                        .toArray(Route[]::new)));
        this.webClient.get().uri("/routes").accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.size()").isEqualTo(DEFAULT_PAGE_SIZE);
    }
    @Test
    public void shouldReturnSecondPageWithTwoRoutes() {
        given(this.routeService.getRoutes())
                .willReturn(Flux.fromArray(IntStream.range(0, 50)
                        .mapToObj(i -> {
                            Route route = new Route();
                            route.setStops(i);
                            return route;
                        })
                        .toArray(Route[]::new)));
        this.webClient.get().uri("/routes?page=2&size=2").accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.size()").isEqualTo(2)
                .jsonPath("$[0].stops").isEqualTo(4)
                .jsonPath("$[1].stops").isEqualTo(5);
    }
    @Test
    public void shouldReturnPageSortedByAirline() {
        Random r = new Random();
        int[] randomNumbers = r.ints(50, 0, 50).toArray();
        given(this.routeService.getRoutes())
                .willReturn(Flux.fromArray(IntStream.range(0, 50)
                        .mapToObj(i -> {
                            Route route = new Route();
                            route.setAirline("A" + String.format("%02d", i));
                            route.setStops(randomNumbers[i]);
                            return route;
                        })
                        .sorted(Comparator.comparing(Route::getStops))
                        .toArray(Route[]::new)));
        this.webClient.get().uri("/routes?sort=airline").accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.size()").isEqualTo(DEFAULT_PAGE_SIZE)
                .jsonPath("$[0].airline").isEqualTo("A00")
                .jsonPath("$[0].stops").isEqualTo(randomNumbers[0])
                .jsonPath("$[1].airline").isEqualTo("A01")
                .jsonPath("$[1].stops").isEqualTo(randomNumbers[1]);
    }
    @Test
    public void shouldReturnPageSortedByAirlineDesc() {
        Random r = new Random();
        int[] randomNumbers = r.ints(50, 0, 5).toArray();
        Route[] routes = IntStream.range(0, 50)
                .mapToObj(i -> {
                    Route route = new Route();
                    route.setAirline("A" + String.format("%02d", i % 5));
                    route.setStops(randomNumbers[i]);
                    return route;
                })
                .toArray(Route[]::new);

        given(this.routeService.getRoutes())
                .willReturn(Flux.fromArray(routes));

        Route[] expected = Arrays.copyOf(routes, routes.length);
        Arrays.sort(expected, Comparator.comparing(Route::getAirline).reversed().thenComparing(Route::getStops));

        WebTestClient.BodyContentSpec bodyContentSpec = this.webClient.get().uri("/routes?sort=airline:desc,stops").accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.size()").isEqualTo(DEFAULT_PAGE_SIZE);
        for (int i = 0; i<=9; i++) {
            bodyContentSpec.jsonPath("$["+i+"].airline").isEqualTo(expected[i].getAirline());
        }
    }
}
