package de.materna.candy_lord.domain;

import io.vavr.Function1;
import io.vavr.Tuple;
import io.vavr.Tuple2;
import io.vavr.collection.List;
import io.vavr.collection.Map;
import io.vavr.control.Either;

public record Player(City city, int cash, Map<CandyType, Integer> candies, int maxCapacity) {
  public Player(City city, int cash, int maxCapacity) {
    this(city, cash, List.of(CandyType.values()).toMap((CandyType type) -> Tuple.of(type, 0)), maxCapacity);
  }

  public Player withCandies(Map<CandyType, Integer> candies) {
    return new Player(city, cash, candies, maxCapacity);
  }

  public Player withCash(int cash) {
    return new Player(city, cash, candies, maxCapacity);
  }

  public Player withCity(City city) {
    return new Player(city, cash, candies, maxCapacity);
  }

  public <T> T map(Function1<Player, T> mapper) {
    return mapper.apply(this);
  }

  public Either<String, Player> buyCandy(CandyType type, int amount) {
    return (candies.values().sum().intValue() + amount > maxCapacity)
        ? Either.left("Your pockets are too small to carry more candies!")
        : city
        .candyPrices()
        .get(type)
        .filter(price -> price * amount <= cash)
        .toEither("You don't have enough money!")
        .map(price -> candyTransaction(type, amount, price));
  }

  public Player visitCity(City city, int ticketPrice) {
    //TODO maybe only allow visits if player has enough cash?
    return withCity(city).withCash(cash - ticketPrice);
  }

  public Tuple2<String, Player> visitCityWithEffect(City city, int ticketPrice, Function1<Player, Tuple2<String, Player>> effect) {
    return effect.apply(visitCity(city, ticketPrice));
  }

  public Either<String, Player> sellCandy(CandyType type, int amount) {
    return (amount > candies.get(type).get())
        ? Either.left("You don't have THAT much candy!")
        : Either.right(city
        .candyPrices()
        .get(type)
        .map(price -> candyTransaction(type, -amount, -price))
        .get());
  }

  private Player candyTransaction(CandyType type, int amount, Integer price) {
    return this
        .withCash(cash - price * amount)
        .withCandies(candies.put(type, candies.get(type).get() + amount));
  }
}
