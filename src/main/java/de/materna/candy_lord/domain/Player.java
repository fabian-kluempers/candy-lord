package de.materna.candy_lord.domain;

import io.vavr.Function1;
import io.vavr.Tuple;
import io.vavr.Tuple2;
import io.vavr.collection.List;
import io.vavr.collection.Map;
import io.vavr.control.Either;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public record Player(City city, int cash, Map<CandyType, Integer> candies, int maxCapacity) {
  public Player(City city, int cash, int maxCapacity) {
    this(city, cash, List.of(CandyType.values()).toMap((CandyType type) -> Tuple.of(type, 0)), maxCapacity);
  }

  @Contract(pure = true)
  public Player withCandies(Map<CandyType, Integer> candies) {
    return new Player(city, cash, candies, maxCapacity);
  }

  @Contract(pure = true)
  public Player withCash(int cash) {
    return new Player(city, cash, candies, maxCapacity);
  }

  @Contract(pure = true)
  public Player withCity(City city) {
    return new Player(city, cash, candies, maxCapacity);
  }

  @Contract(pure = true)
  public Player mapCash(Function1<Integer, Integer> mapper) {
    return withCash(mapper.apply(cash));
  }

  /**
   * Applies the supplied mapping function to the amount of candy.
   * IMPORTANT: this method performs no consistency checks for [maxCapacity]!
   *
   * @param type the [CandyType] to be mapped.
   * @param mapper the mapping function to be applied to the candy amount.
   * @return a new [Player] with the mapped candy amount.
   */
  @Contract(pure = true)
  public Player mapCandyAmount(CandyType type, Function1<Integer, Integer> mapper) {
    return withCandies(candies.put(type, mapper.apply(candies.get(type).get())));
  }

  @Contract(pure = true)
  public Either<String, Player> buyCandy(@NotNull CandyType type, int amount) {
    return (candies.values().sum().intValue() + amount > maxCapacity)
        ? Either.left("Your pockets are too small to carry more candies!")
        : city
        .candyPrices()
        .get(type)
        .filter(price -> price * amount <= cash)
        .toEither("You don't have enough money!")
        .map(price -> candyTransaction(type, amount, price));
  }

  @Contract(pure = true)
  public Player visitCity(@NotNull City city, int ticketPrice) {
    //TODO maybe only allow visits if player has enough cash?
    return withCity(city).withCash(cash - ticketPrice);
  }

  @Contract(pure = true)
  public Tuple2<String, Player> visitCityWithEffect(
      @NotNull City city,
      int ticketPrice,
      @NotNull Function1<Player, Tuple2<String, Player>> effect
  ) {
    return effect.apply(visitCity(city, ticketPrice));
  }

  @Contract(pure = true)
  public Either<String, Player> sellCandy(
      @NotNull CandyType type,
      int amount
  ) {
    return (amount > candies.get(type).get())
        ? Either.left("You don't have THAT much candy!")
        : Either.right(city
        .candyPrices()
        .get(type)
        .map(price -> candyTransaction(type, -amount, price))
        .get());
  }

  @Contract(pure = true)
  private Player candyTransaction(@NotNull CandyType type, int amount, int price) {
    return this
        .withCash(cash - price * amount)
        .withCandies(candies.put(type, candies.get(type).get() + amount));
  }
}
