package com.epam.funwithflags.service;

import com.epam.funwithflags.config.DataProperties;
import com.epam.funwithflags.model.Route;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Objects;
import java.util.function.Function;

@Service
@Slf4j
public class ReactiveRouteService implements RouteService {

    private final List<WebClient> webClients;

    public ReactiveRouteService(DataProperties dataProperties) {
        webClients = dataProperties.getProviders().stream().map(WebClient::create).toList();
        log.info("Found {} providers", webClients.size());
    }

    @Override
    public Flux<Route> getRoutes() {
        return webClients.stream()
                .map(WebClient::get)
                .map(WebClient.RequestHeadersSpec::retrieve)
                .map(responseSpec -> responseSpec.bodyToFlux(Route.class))
                .map(handleErrors())
                .reduce((f1, f2) -> Flux.merge(f1, f2).distinct()).orElse(Flux.empty());
    }

    private static Function<Flux<Route>, Flux<Route>> handleErrors() {
        return routeFlux -> routeFlux.onErrorResume(WebClientResponseException.class,
                ex -> {
                    if (ex.getStatusCode().is4xxClientError()
                    || ex.getStatusCode().is5xxServerError()) {
                        log.warn("Failed to get provider data from url {}", Objects.requireNonNull(ex.getRequest()).getURI(), ex);
                        return Flux.empty();
                    }
                    return Mono.error(ex);
                });
    }
}
