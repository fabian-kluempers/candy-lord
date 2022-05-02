package de.materna.candy_lord.core.control;

import de.materna.candy_lord.core.api.GameAPI;
import de.materna.candy_lord.core.domain.*;
import de.materna.candy_lord.core.dto.StateDTO;
import de.materna.candy_lord.core.dto.EuroRepresentation;
import io.vavr.*;
import io.vavr.collection.List;
import io.vavr.collection.Map;
import io.vavr.collection.Set;
import io.vavr.control.Either;
import io.vavr.control.Option;
import io.vavr.control.Try;

import java.awt.*;
import java.util.Random;
import java.util.function.Predicate;

public class GameController implements GameAPI {
  public static final Map<String, City> cities = List.of(
      new City("Dortmund", new Point(51, 7), 1000, 150),
      new City("Berlin", new Point(52, 13), 1000, 120),
      new City("Frankfurt a.M.", new Point(50, 8), 1000, 170),
      new City("Hamburg", new Point(53, 10), 1000, 140),
      new City("Stuttgart", new Point(48, 9), 1000, 155)
  ).toMap(city -> new Tuple2<>(city.name(), city));

  private static final int MAX_NUM_OF_DAYS = 30;

  private static final Predicate<Integer> END_CONDITION = ref -> ref >= MAX_NUM_OF_DAYS;

  private List<GameState> history;

  public GameController() {
    this(new Random());
  }

  public GameController(Random rng) {
    this.rng = rng;
    newGame();
  }

  private final Random rng;

  @Override public Either<String, StateDTO> visitCity(String city) {
    return state()
        .filterNot(state -> state.player().city().name().equals(city))
        .toEither("You are already in " + city + ".")
        .flatMap(success -> cities.get(city).toEither(city + "is not a valid city"))
        .flatMap(this::visitCity)
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

  @Override public Either<String, StateDTO> undo() {
    return Option.of(history.tail())
        .filterNot(List::isEmpty)
        .peek(tail -> history = tail)
        .toEither("You can't undo right now! Perform an Action first!")
        .flatMap(tail -> state())
        .map(StateMapper::map);
  }

  @Override public StateDTO newGame() {
    history = List.of(
        new GameState(
            new Player(cities.get("Dortmund").get(), 100000, 1000),
            calculateTicketPrices(cities.get("Dortmund").get()),
            0,
            Option.of("Welcome to Candy Lord!")
        )
    );
    return StateMapper.map(state().get());
  }

  @Override public StateDTO getState() {
    return StateMapper.map(state().getOrElse(history.head()));
  }

  @Override public boolean isOver() {
    return END_CONDITION.test(history.head().day());
  }

  @Override public Option<EuroRepresentation> getFinalScore() {
    if (isOver()) {
      Player player = history.head().player();
      int cashForCandies = player
          .candies()
          .foldLeft(0, (acc, entry) -> acc + player.city().candyPrices().get(entry._1).get() * entry._2);
      int score = player.cash() + cashForCandies;
      return Option.of(EuroRepresentation.of(score));
    } else {
      return Option.none();
    }
  }

  @Override public Set<String> getCityNames() {
    return cities.keySet();
  }

  @Override public Set<String> getCandyNames() {
    return List.of(CandyType.values()).map(Enum::name).toSet();
  }

  private Either<String, GameState> visitCity(City city) {
    return state()
        .map(state -> {
              var effectResult = visitWithEffect(
                  city,
                  state.player(),
                  state.ticketPrices().get(city.name()).get(),
                  getRandomEffect()
              );
              return state.visit(
                  effectResult._2,
                  calculateTicketPrices(city),
                  effectResult._1
              );
            }
        )
        .peek(this::updateState);
  }

  private Function1<Player, Tuple2<Option<String>, Player>> getRandomEffect() {
    return List.of(
            Function2.of(Events::mugMoney),
            Function2.of(Events::mugCandy),
            Function2.of(Events::giftMoney),
            Function2.of(Events::giftCandy)
        )
        .get(rng.nextInt(0, 4))
        .apply(rng);
  }

  private Tuple2<Option<String>, Player> visitWithEffect(
      City city,
      Player player,
      int ticketPrice,
      Function1<Player, Tuple2<Option<String>, Player>> effect
  ) {
    return (rng.nextDouble() > 0.5)
        ?
        player.visitCityWithEffect(
            city.withScaledCandyPrices(rng),
            ticketPrice,
            effect
        )
        :
        new Tuple2<>(
            Option.none(),
            player.visitCity(
                city.withScaledCandyPrices(rng),
                ticketPrice
            )
        );
  }

  private Either<String, GameState> sellCandy(CandyType type, int amount) {
    return candyTransaction(Function3.of(Player::sellCandy).reversed().apply(amount, type));
  }

  private Either<String, GameState> buyCandy(CandyType type, int amount) {
    return candyTransaction(Function3.of(Player::buyCandy).reversed().apply(amount, type));
  }

  private Either<String, GameState> candyTransaction(Function1<Player, Either<String, Player>> buyOrSell) {
    return state()
        .flatMap(state -> buyOrSell.apply(state.player()).map(state::withPlayer))
        .peek(this::updateState);
  }

  private Map<String, Integer> calculateTicketPrices(City from) {
    return cities
        .mapValues(city ->
            from.priceTo(city, rng.nextDouble(0.8, 1.2))
        );
  }

  private Either<String, GameState> state() {
    return isOver() ? Either.left("Game is already over!") : Either.right(history.head());
  }

  private void updateState(GameState state) {
    if (!isOver()) history = history.prepend(state);
  }
}
