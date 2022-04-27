package de.materna.candy_lord.core.dto;

import io.vavr.collection.Map;


public record CityDTO(String name, Map<String, Integer> candyPrices) {
}
