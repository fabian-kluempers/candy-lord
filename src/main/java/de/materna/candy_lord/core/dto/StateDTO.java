package de.materna.candy_lord.core.dto;

import io.vavr.collection.Map;
import io.vavr.control.Option;

public record StateDTO(CityDTO city, PlayerDTO player, Map<String, Integer> ticketPrices, Option<String> message, int day) {
}
