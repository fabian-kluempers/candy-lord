package de.materna.candy_lord.control;

import de.materna.candy_lord.api.GameAPI;
import de.materna.candy_lord.domain.CandyType;
import de.materna.candy_lord.domain.City;
import de.materna.candy_lord.domain.GameState;
import de.materna.candy_lord.domain.Player;
import de.materna.candy_lord.dto.StateDTO;
import io.vavr.*;
import io.vavr.collection.List;
import io.vavr.collection.Map;
import io.vavr.control.Either;
import io.vavr.control.Option;
import io.vavr.control.Try;

import java.awt.*;
import java.util.Random;

public class GameController implements GameAPI {
  public static final Map<String, City> cities = List.of(
      new City("Dortmund", new Point(51, 7), 1000, 150),
      new City("Berlin", new Point(52, 13), 1000, 120),
      new City("Frankfurt am Main", new Point(50, 8), 1000, 170),
      new City("Hamburg", new Point(53, 10), 1000, 140),
      new City("Stuttgart", new Point(48, 9), 1000, 155)
  ).toMap(city -> new Tuple2<>(city.name(), city));

  private List<GameState> history;

  public GameController() {
    newGame();
  }

  private static final Random rng = new Random();

  @Override public Either<String, StateDTO> visitCity(String city) {
    System.out.println(history);
    if (state().player().city().name().equals(city)) return Either.left("You are already in " + city + ".");
    else return cities
        .get(city)
        .toEither(city + " is not a valid city.")
        .map(this::visitCity)
        .map(StateMapper::map);
  }

  @Override public Either<String, StateDTO> buyCandy(String candyName, int amount) {
    return Try.of(() -> CandyType.valueOf(candyName))
        .toEither(candyName + "is not a valid candy!")
        .flatMap(type -> buyCandy(type, amount))
        .map(StateMapper::map);
  }

  @Override public Either<String, StateDTO> sellCandy(String candyName, int amount) {
    return Try.of(() -> CandyType.valueOf(candyName))
        .toEither(candyName + "is not a valid candy!")
        .flatMap(type -> sellCandy(type, amount))
        .map(StateMapper::map);
  }

  @Override public Option<StateDTO> undo() {
    return history
        .tailOption()
        .peek(tail -> history = tail)
        .map(List::head)
        .map(StateMapper::map);
  }

  @Override public StateDTO newGame() {
    history = List.of(
        new GameState(
            new Player(cities.get("Dortmund").get(), 100000, 1000),
            calculateTicketPrices(cities.get("Dortmund").get())
        )
    );
    return StateMapper.map(state());
  }

  @Override public StateDTO getState() {
    return StateMapper.map(state());
  }

  private GameState visitCity(City city) {
    updateState(
        new GameState(
            state().player().visitCity(
                city.withScaledCandyPrices(),
                state().ticketPrices().get(city.name()).get()
            ),
            calculateTicketPrices(city)
        )
    );
    return state();
  }

  private Either<String, GameState> sellCandy(CandyType type, int amount) {
    return candyTransaction(Function3.of(Player::sellCandy).reversed().apply(amount, type));
  }

  private Either<String, GameState> buyCandy(CandyType type, int amount) {
    return candyTransaction(Function3.of(Player::buyCandy).reversed().apply(amount, type));
  }

  private Either<String, GameState> candyTransaction(Function1<Player, Either<String, Player>> buyOrSell) {
    return buyOrSell.apply(state().player())
        .map(player -> state().withPlayer(player))
        .peek(this::updateState);
  }

  private static Map<String, Integer> calculateTicketPrices(City from) {
    return cities
        .mapValues(city ->
            from.priceTo(city, rng.nextDouble(0.8, 1.2))
        );
  }

  private GameState state() {
    return history.head();
  }

  private void updateState(GameState state) {
    history = history.prepend(state);
  }
}
