package de.materna.candy_lord.domain;

import io.vavr.collection.HashMap;
import org.junit.jupiter.api.Test;

import java.awt.*;

import static org.junit.jupiter.api.Assertions.*;

class PlayerTest {
  private static final Player initialPlayer = new Player(new City("Dortmund", HashMap.of(CandyType.MARSHMALLOW, 5), new Point(0,0), 7), 0, 10);

  @Test
  void buyCandy() {
    var x = initialPlayer.withCash(20).buyCandy(CandyType.MARSHMALLOW, 10);
    assertTrue(x.isLeft());
  }

  @Test
  void sellCandy() {
  }
}