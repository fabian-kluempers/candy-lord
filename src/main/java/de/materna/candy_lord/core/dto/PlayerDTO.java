package de.materna.candy_lord.core.dto;

import io.vavr.collection.Map;

public record PlayerDTO(EuroRepresentation cash, int maxCapacity, Map<String, Integer> candies) {
}
