package de.materna.candy_lord.core.api;

import de.materna.candy_lord.core.control.GameController;
import de.materna.candy_lord.core.dto.StateDTO;
import de.materna.candy_lord.core.dto.EuroRepresentation;
import io.vavr.collection.Set;
import io.vavr.control.Either;
import io.vavr.control.Option;

public interface GameAPI {
  /**
   * Ties to visit the city with the supplied name.
   * Returns either a new {@link StateDTO} representing the new game state or an error message.
   * Some random events may happen during the visit. If such an event occurred,
   * a description of the event is available in the <code>StateDTO.message()</code>.
   *
   * @param cityName the city to visit.
   * @return either a new game state or an error message
   */
  Either<String, StateDTO> visitCity(String cityName);

  /**
   * Tries to buy <code>amount</code> candies of the supplied candyName.
   * Returns either a new {@link StateDTO} representing the new game state or an error message.
   * An error message will be returned if the Player does not have enough capacity or cash or
   * if the candyName does not identify a valid candy.
   *
   * @param candyName the candy to buy.
   * @param amount the amount of candy to buy.
   * @return either a new game state or an error message
   */
  Either<String, StateDTO> buyCandy(String candyName, int amount);

  /**
   * Tries to sell <code>amount</code> candies of the supplied candyName.
   * Returns either a new {@link StateDTO} representing the new game state or an error message.
   * An error message will be returned if the Player does not have enough of that candy or
   * if the candyName does not identify a valid candy.
   *
   * @param candyName the candy to sell.
   * @param amount the amount of candy to sell.
   * @return either a new game state or an error message
   */
  Either<String, StateDTO> sellCandy(String candyName, int amount);

  /**
   * Revokes the last action (travel, buy, sell).
   * Returns a {@link StateDTO} representing the previous game state or nothing if a revocation is not possible.
   * The revocation might fail if no actions have been performed yet.
   *
   * @return either the previous game state or nothing.
   */
  Option<StateDTO> undo();

  /**
   * Starts a new Game and returns a {@link StateDTO} representing the initial game state.
   *
   * @return the initial game state.
   */
  StateDTO newGame();

  /**
   * Returns a {@link StateDTO} representing the current game state.
   *
   * @return the current game state.
   */
  StateDTO getState();

  /**
   * Returns true if the game is over or false otherwise.
   * No new actions should be performed on a game that is over.
   * @return whether the game is over or not.
   */
  boolean isOver();

  /**
   * Returns a EuroRepresentation of the final score or nothing if the game is still in progress.
   * @return Either a EuroRepresentation of the final score or nothing.
   */
  Option<EuroRepresentation> getFinalScore();

  /**
   * Returns all available city names for this game.
   *
   * @see #visitCity
   * @return all available city names for this game.
   */
  Set<String> getCityNames();

  /**
   * Returns all available candy names for this game.
   *
   * @see #buyCandy
   * @see #sellCandy
   * @return all available candy names for this game.
   */
  Set<String> getCandyNames();

  /**
   * Creates a new GameAPI. The concrete Implementation in use is {@link GameController}.
   * @return a new GameAPI.
   */
  static GameAPI create() {
    return new GameController();
  }
}
