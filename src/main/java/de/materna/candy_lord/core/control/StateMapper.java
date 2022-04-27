package de.materna.candy_lord.core.control;

import de.materna.candy_lord.core.domain.GameState;
import de.materna.candy_lord.core.dto.CityDTO;
import de.materna.candy_lord.core.dto.PlayerDTO;
import de.materna.candy_lord.core.dto.StateDTO;

class StateMapper {
  /**
   * Maps all relevant Information of a {@link GameState} that can and should be exposed to the GUI/Frontend to a {@link StateDTO}.
   * @param state the GameState to map.
   * @return the StateDTO.
   */
  public static StateDTO map(GameState state) {
    return new StateDTO(
        new CityDTO(
            state.player().city().name(),
            state.player().city().candyPrices().mapKeys(Enum::name)
        ),
        new PlayerDTO(
            state.player().cash(),
            state.player().maxCapacity(),
            state.player().candies().mapKeys(Enum::name)
        ),
        state.ticketPrices(),
        state.message(),
        state.day()
    );
  }
}
