package de.materna.candy_lord.command_line_app;

public class EuroRepresentation {
  public final int euro;
  public final int cent;

  private EuroRepresentation(int euro, int cent) {
    this.euro = euro;
    this.cent = cent;
  }

  public static EuroRepresentation of(int cent) {
    return new EuroRepresentation(cent / 100, Math.abs(cent % 100));
  }
}