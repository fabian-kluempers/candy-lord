package de.materna.candy_lord.domain;

import io.vavr.collection.Map;
import io.vavr.control.Option;

public record GameState(Player player, Map<String, Integer> ticketPrices, int day, Option<String> message) {

  /**
   * Returns a new GameState with adjusted player.
   * IMPORTANT: resets the <code>message</code> to <code>Option.none</code>.
   *
   * @param player the new player state.
   * @return a new GameState with the new ticketPrices and no message.
   */
  public GameState withPlayer(Player player) {
    return new GameState(player, ticketPrices, day, Option.none());
  }

  /**
   * Returns a new GameState with adjusted ticketPrices.
   * IMPORTANT: resets the <code>message</code> to <code>Option.none</code>.
   *
   * @param ticketPrices the new ticketPrices.
   * @return a new GameState with the new ticketPrices and no message.
   */
  public GameState withTicketPrices(Map<String, Integer> ticketPrices) {
    return new GameState(player, ticketPrices, day, Option.none());
  }

  /**
   * This method increments the <code>day</code> in the new state.
   * This method should be used to model a state transformation after a city visit.
   *
   * @param player the new state of the player.
   * @param ticketPrices the new ticket prices.
   * @param message the new message.
   * @return a new GameState with the new player, ticketPrices, message and incremented day.
   */
  public GameState visit(Player player, Map<String, Integer> ticketPrices, Option<String> message) {
    return new GameState(player, ticketPrices, day + 1, message);
  }
}
