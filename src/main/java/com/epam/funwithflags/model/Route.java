package com.epam.funwithflags.model;

import lombok.Data;

import java.util.Optional;

@Data
public class Route {
    String airline;
    String sourceAirport;
    String destinationAirport;
    String codeShare;
    int stops;
    Optional<String> equipment;
}
