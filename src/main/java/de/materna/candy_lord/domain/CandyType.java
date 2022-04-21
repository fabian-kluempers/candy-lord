package de.materna.candy_lord.domain;

public enum CandyType {
  CHOCOLATE(2), CAKE(5), MARSHMALLOW(1), LICORICE(4), LEMONADE(3);

  public final int basePrice;

  CandyType(int basePrice) {
    this.basePrice = basePrice;
  }
}
