package de.materna.candy_lord.domain;

import de.materna.candy_lord.util.EuroRepresentation;
import io.vavr.Tuple2;
import io.vavr.control.Option;

import java.util.Random;

public class Events {
  public static Tuple2<Option<String>, Player> mugMoney(Random rng, Player player) {
    String message = null;
    Player newPlayer = player;
    if (player.cash() > 0) {
      int amount = rng.nextInt(0, player.cash() + 1);
      newPlayer = newPlayer.mapCash(x -> x - amount);
      EuroRepresentation euroRepresentation = EuroRepresentation.of(amount);
      message = String.format("You got mugged! You Lost %d.%02d€!", euroRepresentation.euro, euroRepresentation.cent);
    }
    return new Tuple2<>(Option.of(message), newPlayer);
  }

  public static Tuple2<Option<String>, Player> giftMoney(Random rng, Player player) {
    //gift player random amount in [0€,5000€]
    int amount = (int) Math.round(rng.nextDouble() * 500_000);
    EuroRepresentation euroRepresentation = EuroRepresentation.of(amount);
    return new Tuple2<>(
        Option.of(String.format("You found %d.%02d€!", euroRepresentation.euro, euroRepresentation.cent)),
        player.mapCash(x -> x + amount)
    );
  }

  public static Tuple2<Option<String>, Player> mugCandy(Random rng, Player player) {
    return player.candies().maxBy(Tuple2::_2).map(entry -> {
      int amount = rng.nextInt(0, entry._2 + 1);
      return amount > 0
          ?
          new Tuple2<>(
              Option.of((String.format("You got mugged! They took %d %s from you!", amount, entry._1))),
              player.mapCandyAmount(entry._1, x -> x - amount)
          )
          :
          new Tuple2<Option<String>, Player>(Option.none(), player);
    }).get();
  }

  public static Tuple2<Option<String>, Player> giftCandy(Random rng, Player player) {
    return player.candies().minBy(Tuple2::_2).map(entry -> {
      System.out.println(entry);
      System.out.println(player.remainingCapacity());
          int amount = Math.min(rng.nextInt(entry._2, player.maxCapacity()), player.remainingCapacity());
          return amount > 0
              ?
              new Tuple2<>(
                  Option.of((String.format("You found %d %s!", amount, entry._1))),
                  player.mapCandyAmount(entry._1, x -> x + amount)
              )
              :
              new Tuple2<Option<String>, Player>(Option.none(), player);
        }
    ).get();
  }
}
