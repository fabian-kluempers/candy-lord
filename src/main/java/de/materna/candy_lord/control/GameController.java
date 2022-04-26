package de.materna.candy_lord.control;

import de.materna.candy_lord.api.GameAPI;
import de.materna.candy_lord.domain.*;
import de.materna.candy_lord.dto.StateDTO;
import de.materna.candy_lord.util.EuroRepresentation;
import io.vavr.Function1;
import io.vavr.Function2;
import io.vavr.Function3;
import io.vavr.Tuple2;
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

  private static final Predicate<Integer> END_CONDITION = (ref) -> ref >= MAX_NUM_OF_DAYS;

  private List<GameState> history;

  public GameController() {
    newGame();
  }

  private static final Random rng = new Random();

  @Override public Either<String, StateDTO> visitCity(String city) {
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
    return Option.of(history.tail())
        .filterNot(List::isEmpty)
        .peek(tail -> history = tail)
        .map(List::head)
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
    return StateMapper.map(state());
  }

  @Override public StateDTO getState() {
    return StateMapper.map(state());
  }

  @Override public boolean isOver() {
    return END_CONDITION.test(state().day());
  }

  @Override public Option<String> getFinalScoreDescription() {
    if (isOver()) {
      Player player = state().player();
      int cashForCandies = player
          .candies()
          .foldLeft(0, (acc, entry) -> acc + player.city().candyPrices().get(entry._1).get() * entry._2);
      int score = player.cash() + cashForCandies;
      EuroRepresentation euroRep = EuroRepresentation.of(score);
      return Option.of(String.format(
          "Your final cash amount after selling all candies is: %d.%2d€",
          euroRep.euro,
          euroRep.cent
      ));
    } else return Option.none();
  }

  @Override public Set<String> getCityNames() {
    return cities.keySet();
  }

  @Override public Set<String> getCandyNames() {
    return List.of(CandyType.values()).map(Enum::name).toSet();
  }

  private GameState visitCity(City city) {
    // Choose one effect
    Function1<Player, Tuple2<Option<String>, Player>> effect = List.of(
            Function2.of(Events::mugMoney),
            Function2.of(Events::mugCandy),
            Function2.of(Events::giftMoney),
            Function2.of(Events::giftCandy)
        )
        .get(rng.nextInt(0, 4))
        .apply(rng); // partially apply the rng
    // Trigger effect with a chance of 50%
    Tuple2<Option<String>, Player> effectResult = (rng.nextDouble() > 0.5)
        ?
        state().player().visitCityWithEffect(
            city.withScaledCandyPrices(rng),
            state().ticketPrices().get(city.name()).get(),
            effect
        )
        :
        new Tuple2<>(
            Option.none(),
            state().player().visitCity(
                city.withScaledCandyPrices(rng),
                state().ticketPrices().get(city.name()).get()
            )
        );

    updateState(
        state().visit(
            effectResult._2,
            calculateTicketPrices(city),
            effectResult._1
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
