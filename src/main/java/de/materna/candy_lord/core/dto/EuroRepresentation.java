package de.materna.candy_lord.core.dto;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class EuroRepresentation {
  private final int totalCent;
  public final int euro;
  public final int cent;

  private EuroRepresentation(int cent) {
    this.totalCent = cent;
    this.euro = cent / 100;
    this.cent = Math.abs(cent % 100);
  }

  public BigDecimal asBigDecimal() {
    return BigDecimal.valueOf(totalCent).divide(BigDecimal.valueOf(100), 2, RoundingMode.CEILING);
  }

  public static EuroRepresentation of(int cent) {
    return new EuroRepresentation(cent);
  }
}