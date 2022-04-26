package de.materna.candy_lord.control;

import de.materna.candy_lord.api.GameAPI;
import de.materna.candy_lord.domain.CandyType;
import de.materna.candy_lord.dto.StateDTO;
import de.materna.candy_lord.util.GuiRenderer;
import de.materna.candy_lord.util.TupleUtil;
import io.vavr.API;
import io.vavr.Function1;
import io.vavr.Function3;
import io.vavr.Tuple2;
import io.vavr.collection.List;
import io.vavr.collection.Map;
import io.vavr.control.Either;
import io.vavr.control.Try;
import io.vavr.control.Validation;

import static io.vavr.API.$;
import static io.vavr.API.Case;


public class IOController {
  private final GameAPI game;

  private final Map<Integer, String> IndexToCity;

  private final Map<Integer, CandyType> IndexToCandy;

  public IOController(GameAPI game) {
    this.game = game;
    var cityToIndex = game.getCityNames().toList()
        .sortBy(String::length)
        .reverse()
        .zipWithIndex()
        .toMap(entry -> entry.map2(x -> x + 1));
    var candyToIndex = List.of(CandyType.values())
        .sortBy(x -> -x.name().length()) // descending (notice -length)
        .zipWithIndex()
        .toMap(entry -> entry.map2(x -> x + 1));
    this.render = Function3.of(GuiRenderer::render)
        .reversed()
        .apply(
            cityToIndex,
            candyToIndex
        );
    this.IndexToCity = cityToIndex.toMap(TupleUtil::flip);
    this.IndexToCandy = candyToIndex.toMap(TupleUtil::flip);
  }


  public String parse(String input) {
    String match2Args = "\s+\\w+\s+\\w+\s?";
    String trimmedInput = input.trim();
    if (trimmedInput.matches("[tT]\s+\\w+")) {
      return travel(trimmedInput);
    } else if (trimmedInput.matches("[sS]" + match2Args)) {
      return sell(trimmedInput);
    } else if (trimmedInput.matches("[bB]" + match2Args)) {
      return buy(trimmedInput);
    } else {
      return malformedCommand();
    }
  }

  public String renderState(StateDTO state) {
    return render.apply(state);
  }

  private final Function1<StateDTO, String> render;


  private String undo() {
    return game
        .undo()
        .map(render)
        .getOrElse("You can't undo right now!");
  }

  private String buy(String input) {
    var args = input.split("\s+", 3);
    var validation = validateMapIndex(args[1], IndexToCandy)
        .combine(validateInt(args[2]))
        .ap((index, amount) -> game.buyCandy(IndexToCandy.get(index).get().name(), amount));

    if (validation.isValid()) {
      Either<String, StateDTO> result = validation.get();
      return result.isRight() ? render.apply(result.get()) : result.getLeft();
    } else
      return validation.getError().reduce((x, y) -> x + "\n" + y);
  }

  private String sell(String input) {
    var args = input.split("\s+", 3);
    var validation = validateMapIndex(args[1], IndexToCandy)
        .combine(validateInt(args[2]))
        .ap((index, amount) -> game.sellCandy(IndexToCandy.get(index).get().name(), amount));

    if (validation.isValid()) {
      Either<String, StateDTO> result = validation.get();
      return result.isRight() ? render.apply(result.get()) : result.getLeft();
    } else
      return validation.getError().reduce((x, y) -> x + "\n" + y);
  }

  private static Validation<String, Integer> validateInt(String arg) {
    return Validation.fromEither(Try.of(() -> Integer.parseInt(arg)).toEither("Please supply a valid number as the second Argument!"));
  }

  private static Validation<String, Integer> validateMapIndex(String arg, Map<Integer, ?> map) {
    return Validation.fromEither(Try.of(() -> Integer.parseInt(arg))
        .toEither("Please supply a valid number as the first argument!")
        .flatMap(x -> map.containsKey(x)
            ? Either.right(x)
            : Either.left("Please supply a number that is in " + map.keySet().toList().sorted() + " as the first argument!")
        )
    );
  }

  private String travel(String input) {
    var arg = input.split("\s+", 2)[1];
    Validation<String, Either<String, StateDTO>> validation = validateMapIndex(arg, IndexToCity)
        .map((index) -> game.visitCity(IndexToCity.get(index).get()));

    if (validation.isValid()) {
      Either<String, StateDTO> result = validation.get();
      return result.isRight() ? render.apply(result.get()) : result.getLeft();
    } else return validation.getError();
  }

  private String malformedCommand() {
    return "Please supply a valid command!";
  }
}
