package de.materna.candy_lord.domain;

import io.vavr.collection.Map;
import io.vavr.control.Option;

public record GameState(Player player, Map<String, Integer> ticketPrices, int day, Option<String> message) {

  public GameState withPlayer(Player player) {
    return new GameState(player, ticketPrices, day, Option.none());
  }

  public GameState withTicketPrices(Map<String, Integer> ticketPrices) {
    return new GameState(player, ticketPrices, day, Option.none());
  }

  public GameState visit(Player player, Map<String, Integer> ticketPrices, Option<String> message) {
    return new GameState(player, ticketPrices, day + 1, message);
  }
}
