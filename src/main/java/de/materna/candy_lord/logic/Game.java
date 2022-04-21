package de.materna.candy_lord.logic;

import de.materna.candy_lord.domain.City;
import de.materna.candy_lord.domain.Player;
import io.vavr.Tuple2;
import io.vavr.collection.List;
import io.vavr.collection.Map;
import io.vavr.control.Either;

import java.awt.*;
import java.util.Random;

public class Game {
  public static final Map<String, City> cities = List.of(
      new City("Dortmund", new Point(51, 7), 1000, 150),
      new City("Berlin", new Point(52, 13), 1000, 120),
      new City("Frankfurt am Main", new Point(50, 8), 1000, 170),
      new City("Hamburg", new Point(53, 10), 1000, 140),
      new City("Stuttgart", new Point(48, 9), 1000, 155)
  ).toMap(city -> new Tuple2<>(city.name(), city));

  private List<Player> history = List.of(
      new Player(cities.get("Dortmund").get(), 100000, 1000)
  );

  private final Random rng = new Random();

  private Map<String, Integer> ticketPrices = calculateTicketPrices();

  Either<String, Game> visitCity(String city) {
    if (player().city().name().equals(city)) return Either.left("Du bist doch bereits in " + city + ".");
    else return cities
        .get(city)
        .toEither(city + " ist kein gültiger Städttename.")
        .map(this::visitCity);
  }

  private Game visitCity(City city) {
    history = history.prepend(
        player().visitCity(
            city.withScaledCandyPrices(),
            ticketPrices.get(city.name()).get()
        ));
    ticketPrices = calculateTicketPrices();
    return this;
  }

  private Map<String, Integer> calculateTicketPrices() {
    return cities
        .mapValues(city ->
            player()
                .city()
                .priceTo(city, rng.nextDouble(0.8, 1.2))
        );
  }

  public Map<String, Integer> getTicketPrices() {
    return ticketPrices;
  }

  public Player getPlayer() {
    return player();
  }

  private Player player() {
    return history.head();
  }
}
