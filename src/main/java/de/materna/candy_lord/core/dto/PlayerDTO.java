package de.materna.candy_lord.core.dto;

import io.vavr.collection.Map;

public record PlayerDTO(int cash, int maxCapacity, Map<String, Integer> candies) {
}
