package de.materna.candy_lord.core.domain;

import io.vavr.Tuple2;
import io.vavr.collection.HashMap;
import io.vavr.control.Option;
import org.junit.jupiter.api.Test;

import java.awt.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PlayerTest {
  private static final Player initialPlayer = new Player(
      new City("Dortmund", HashMap.of(CandyType.MARSHMALLOW, 5), new Point(0, 0), 7),
      0,
      10
  );

  @Test void buyCandy() {
    var testNotEnoughMoney = initialPlayer.withCash(20).buyCandy(CandyType.MARSHMALLOW, 10);
    assertTrue(testNotEnoughMoney.isLeft());
    assertEquals("You don't have enough money!", testNotEnoughMoney.getLeft());
    var testValid = initialPlayer.withCash(5000).buyCandy(CandyType.MARSHMALLOW, 9);
    assertTrue(testValid.isRight());
    assertEquals(9, testValid.get().candies().get(CandyType.MARSHMALLOW).get());
    var testNotEnoughCapacity = testValid.get().buyCandy(CandyType.MARSHMALLOW, 2);
    assertTrue(testNotEnoughCapacity.isLeft());
    assertEquals("Your pockets are too small to carry more candies!", testNotEnoughCapacity.getLeft());
  }

  @Test void sellCandy() {
    var playerWith10MM = initialPlayer
        .withCash(0)
        .withCandies(initialPlayer.candies().put(CandyType.MARSHMALLOW, 10));
    var testAmountToHigh = playerWith10MM.sellCandy(CandyType.MARSHMALLOW, 11);
    assertTrue(testAmountToHigh.isLeft());
    assertEquals("You don't have THAT much candy!", testAmountToHigh.getLeft());
    var testValid = playerWith10MM.sellCandy(CandyType.MARSHMALLOW, 9);
    assertTrue(testValid.isRight());
    assertEquals(1, testValid.get().candies().get(CandyType.MARSHMALLOW).get());
    assertEquals(45, testValid.get().cash());
  }

  @Test void visitCity() {
    var otherCity = new City("Berlin", new Point(0, 0), 1, 1);
    var playerInNewCity = initialPlayer.withCash(10).visitCity(otherCity, 5);
    assertEquals(otherCity, playerInNewCity.city());
    assertEquals(5, playerInNewCity.cash());
  }

  @Test void visitCityWithEffect() {
    var otherCity = new City("Berlin", new Point(0, 0), 1, 1);
    var result = initialPlayer
        .visitCityWithEffect(
            otherCity,
            5,
            (player) -> new Tuple2<>(Option.of("You found 10cent!"), player.mapCash(x -> x + 10))
        );
    assertEquals(otherCity, result._2.city());
    assertEquals(5, result._2.cash());
    assertEquals("You found 10cent!", result._1.get());
    result = initialPlayer
        .visitCityWithEffect(
            otherCity,
            5,
            (player -> new Tuple2<>(Option.of("You found 5 Chocolates!"), player.mapCandyAmount(CandyType.CHOCOLATE, x -> x + 5)))
        );
    assertEquals(otherCity, result._2.city());
    assertEquals(-5, result._2.cash());
    assertEquals("You found 5 Chocolates!", result._1.get());
  }
}