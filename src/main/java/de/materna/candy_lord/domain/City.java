package de.materna.candy_lord.domain;


import io.vavr.Tuple;
import io.vavr.Tuple2;
import io.vavr.collection.List;
import io.vavr.collection.Map;

import java.awt.*;

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

  public City withCandyPrices(Map<CandyType, Integer> candyPrices) {
    return new City(name, candyPrices, coordinates, baseTicketPrice);
  }

  private double distanceTo(City city) {
    System.out.println(coordinates.distance(city.coordinates));
    return coordinates.distance(city.coordinates);
  }

  public int priceTo(City city, double scalar) {
    return (int) Math.round((distanceTo(city)) * baseTicketPrice * scalar);
  }

  public City withScaledCandyPrices(Map<CandyType, Double> scalars) {
    return withCandyPrices(
        List.of(CandyType.values()).toMap(type ->
            new Tuple2<>(type, (int) Math.round(candyPrices.get(type).get() * scalars.get(type).get()))
        ));
  }

  public City withScaledCandyPrices() {
    return withScaledCandyPrices(generateRandomScalars());
  }

  public static Map<CandyType, Double> generateRandomScalars() {
    return List.of(CandyType.values()).toMap((x) -> new Tuple2<>(x, Math.random() * 0.4 + 1));
  }
}
