package com.epam.funwithflags.controller;

import com.epam.funwithflags.model.Route;
import com.epam.funwithflags.service.RouteService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.util.Comparator;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class RouteController {
    public static final int DEFAULT_PAGE_SIZE = 10;
    public static final Comparator<Route> DEFAULT_ROUTE_COMPARATOR = Comparator.comparing(Route::getStops);
    private final RouteService routeService;

    @GetMapping("/routes")
    public Flux<Route> getRoutes(@RequestParam(value = "page", defaultValue = "0") long page,
                                 @RequestParam(value = "size", defaultValue = "" + DEFAULT_PAGE_SIZE) long size,
                                 @RequestParam(value = "sort", defaultValue = "") List<String> sort) {
        return routeService.getRoutes()
                .sort(buildComparator(sort))
                .skip(page * size).take(size);
    }

    private Comparator<? super Route> buildComparator(List<String> sortConditions) {
        return sortConditions.stream().limit(3)
                .map(this::toComparator)
                .reduce(Comparator::thenComparing).orElse(DEFAULT_ROUTE_COMPARATOR);
    }

    private Comparator<Route> toComparator(String sortParam) {
        String[] fieldAndDirection = sortParam.split(":");
        String fieldName;
        String direction;
        if(fieldAndDirection.length == 1) {
            fieldName = fieldAndDirection[0];
            direction = "asc";
        } else if (fieldAndDirection.length == 2) {
            fieldName = fieldAndDirection[0];
            direction = fieldAndDirection[1];
        } else {
            return DEFAULT_ROUTE_COMPARATOR;
        }
        Comparator<Route> comparator = switch (fieldName) {
            case "airline" -> Comparator.comparing(Route::getAirline);
            case "sourceAirport" -> Comparator.comparing(Route::getSourceAirport);
            case "destinationAirport" -> Comparator.comparing(Route::getDestinationAirport);
            default -> DEFAULT_ROUTE_COMPARATOR;
        };

        return "asc".equalsIgnoreCase(direction) ? comparator : comparator.reversed();
    }
}
