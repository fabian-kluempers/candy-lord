package de.materna.candy_lord.control;

import de.materna.candy_lord.api.GameAPI;
import de.materna.candy_lord.dto.StateDTO;
import de.materna.candy_lord.util.GuiRenderer;
import io.vavr.Tuple2;
import io.vavr.control.Either;
import io.vavr.control.Try;

public class IOController {
  private GameAPI game;

  public IOController(GameAPI game) {
    this.game = game;
  }

  public String parse(String input) {
    var args = input.split(" ", 2);
    var trimmed = new Tuple2<>(args[0].trim(), args[1].trim());
    return switch (trimmed._1) {
      case "t" -> travel(trimmed._2);
      case "s" -> sell(trimmed._2);
      case "b" -> buy(trimmed._2);
      default -> malformedCommand();
    };
  }

  private String buy(String input) {
    var args = input.split(" ", 2);
    var result = Try.of(() -> Integer.parseInt(args[1]))
        .toEither("Please supply a valid Number as the second Argument!")
        .flatMap(amount -> game.buyCandy(args[0], amount));

    if (result.isRight()) {
      return GuiRenderer.render(result.get());
    } else {
      return result.getLeft();
    }
  }

  private String sell(String input) {
    var args = input.split(" ", 2);
    var result = Try.of(() -> Integer.parseInt(args[1]))
        .toEither("Please supply a valid Number as the second Argument!")
        .flatMap(amount -> game.sellCandy(args[0], amount));

    if (result.isRight())
      return GuiRenderer.render(result.get());
    else
      return result.getLeft();
  }

  private String travel(String input) {
    Either<String, StateDTO> result = game.visitCity(input);

    if (result.isRight())
      return GuiRenderer.render(result.get());
    else {
      return result.getLeft();
    }
  }

  private String malformedCommand() {
    return "Please supply a valid command!";
  }
}
