package de.materna.candy_lord.api;

import de.materna.candy_lord.control.GameController;
import de.materna.candy_lord.dto.StateDTO;
import io.vavr.control.Either;
import io.vavr.control.Option;

public interface GameAPI {
  Either<String, StateDTO> visitCity(String cityName);

  Either<String, StateDTO> buyCandy(String candyName, int amount);

  Either<String, StateDTO> sellCandy(String candyName, int amount);

  Option<StateDTO> undo();

  StateDTO newGame();

  StateDTO getState();

  boolean isOver();

  Option<String> getFinalScoreDescription();

  static GameAPI create() {
    return new GameController();
  }
}
