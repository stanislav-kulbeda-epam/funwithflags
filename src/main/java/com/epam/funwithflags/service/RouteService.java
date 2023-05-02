package com.epam.funwithflags.service;

import com.epam.funwithflags.model.Route;
import reactor.core.publisher.Flux;

public interface RouteService {
    Flux<Route> getRoutes();
}
