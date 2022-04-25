package de.materna.candy_lord.control;

import de.materna.candy_lord.domain.GameState;
import de.materna.candy_lord.dto.CityDTO;
import de.materna.candy_lord.dto.PlayerDTO;
import de.materna.candy_lord.dto.StateDTO;

class StateMapper {
  public static StateDTO map(GameState state) {
    return new StateDTO(
        new CityDTO(
            state.player().city().name(),
            state.player().city().candyPrices()
        ),
        new PlayerDTO(
            state.player().cash(),
            state.player().maxCapacity(),
            state.player().candies()
        ),
        state.ticketPrices(),
        state.message(),
        state.day()
    );
  }
}
