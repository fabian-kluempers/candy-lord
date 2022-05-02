package de.materna.candy_lord.core.domain;


import io.vavr.Tuple;
import io.vavr.Tuple2;
import io.vavr.collection.List;
import io.vavr.collection.Map;
import org.jetbrains.annotations.Contract;

import java.awt.*;
import java.util.Random;

public record City(String name, Map<CandyType, Integer> candyPrices, Point coordinates, int baseTicketPrice) {
  public City(String name, Point coordinates, int baseTicketPrice, int candyBasePriceScalar) {
    this(
        name,
        List.of(CandyType.values())
            .toMap((CandyType type) -> Tuple.of(type, type.basePrice * candyBasePriceScalar)),
        coordinates,
        baseTicketPrice
    );
  }

  @Contract(pure = true)
  public City withCandyPrices(Map<CandyType, Integer> candyPrices) {
    return new City(name, candyPrices, coordinates, baseTicketPrice);
  }

  @Contract(pure = true)
  private double distanceTo(City city) {
    return coordinates.distance(city.coordinates);
  }

  @Contract(pure = true)
  public int priceTo(City city, double scalar) {
    return (int) Math.round((distanceTo(city)) * baseTicketPrice * scalar);
  }

  @Contract(pure = true)
  public City withScaledCandyPrices(Map<CandyType, Double> scalars) {
    return withCandyPrices(
        List.of(CandyType.values()).toMap(type ->
            new Tuple2<>(type, (int) Math.round(candyPrices.get(type).get() * scalars.get(type).get()))
        ));
  }

  public City withScaledCandyPrices(Random rng) {
    return withScaledCandyPrices(generateRandomScalars(rng));
  }

  public static Map<CandyType, Double> generateRandomScalars(Random rng) {
    return List.of(CandyType.values()).toMap(x -> new Tuple2<>(x, rng.nextDouble() * 0.4 + 1));
  }
}
