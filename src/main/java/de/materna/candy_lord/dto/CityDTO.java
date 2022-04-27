package de.materna.candy_lord.dto;

import de.materna.candy_lord.domain.CandyType;
import io.vavr.collection.Map;


public record CityDTO(String name, Map<String, Integer> candyPrices) {
}
