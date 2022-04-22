package de.materna.candy_lord.domain;

import io.vavr.collection.Map;

public record GameState(Player player, Map<String, Integer> ticketPrices) {
  public GameState withPlayer(Player player) {
    return new GameState(player, ticketPrices);
  }
  public GameState withTicketPrices(Map<String, Integer> ticketPrices) {
    return new GameState(player, ticketPrices);
  }
}
