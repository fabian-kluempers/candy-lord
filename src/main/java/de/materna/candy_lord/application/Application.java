package de.materna.candy_lord.application;

import de.materna.candy_lord.domain.City;
import de.materna.candy_lord.domain.Player;
import io.vavr.Tuple2;
import io.vavr.collection.List;
import io.vavr.collection.Map;

import java.awt.*;
import java.util.Scanner;

public class Application {
  public static final Map<String, City> cities = List.of(
      new City("Dortmund", new Point(51,7), 1000, 150),
      new City("Berlin", new Point(52,13), 1000, 120),
      new City("Frankfurt am Main", new Point(50,8), 1000, 170),
      new City("Hamburg", new Point(53,10), 1000, 140),
      new City("Stuttgart", new Point(48,9), 1000, 155)
  ).toMap(city -> new Tuple2<>(city.name(), city));

  public static void main(String[] args) {
    Scanner scanner = new Scanner(System.in);
    Player player = new Player(cities.get("Dortmund").get(), 4000, 100);
    while (true) {
      String input = scanner.nextLine();
      switch (input) {
        case "Berlin" -> player = player.visitCity(cities.get("Berlin").get().withScaledCandyPrices(City.generateRandomScalars()), player.city().priceTo(cities.get("Berlin").get(), Math.random() + 0.5));
        case "Frankfurt am Main" -> player = player.visitCity(cities.get("Frankfurt am Main").get().withScaledCandyPrices(City.generateRandomScalars()), player.city().priceTo(cities.get("Frankfurt am Main").get(), Math.random() + 0.5));
        case "Hamburg" -> player = player.visitCity(cities.get("Hamburg").get().withScaledCandyPrices(City.generateRandomScalars()), player.city().priceTo(cities.get("Hamburg").get(), Math.random() + 0.5));
        case "Dortmund" -> player = player.visitCity(cities.get("Dortmund").get().withScaledCandyPrices(City.generateRandomScalars()), player.city().priceTo(cities.get("Dortmund").get(), Math.random() + 0.5));
        case "Stuttgart" -> player = player.visitCity(cities.get("Stuttgart").get().withScaledCandyPrices(City.generateRandomScalars()), player.city().priceTo(cities.get("Stuttgart").get(), Math.random() + 0.5));
      }
      System.out.println(player);
    }
  }

}
