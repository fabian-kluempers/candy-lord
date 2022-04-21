package de.materna.candy_lord.logic;

import de.materna.candy_lord.util.GuiRenderer;
import io.vavr.API;
import io.vavr.collection.List;
import io.vavr.control.Either;
import io.vavr.control.Try;


import static io.vavr.API.$;
import static io.vavr.API.Case;

public class IOController {
  private Game game;

  public IOController(Game game) {
    this.game = game;
  }

  public String parse(String input) {
    var args = List.of(input.split(" ")).map(String::trim);
    return switch (args.head()) {
      case "t" -> travel(args.tail());
      case "s" -> sell(args.tail());
      case "b" -> buy(args.tail());
      default -> malformedCommand();
    };
  }

  private String buy(List<String> args) {
    return "NYI";
  }

  private String sell(List<String> args) {
    return "NYI";
  }

  private String travel(List<String> args) {
    Either<String,Game> result = args.isEmpty()
        ? Either.left("Bitte gib ein weiteres Argument an.")
        : game.visitCity(args.head());

    if (result.isLeft())
      return GuiRenderer.render(game.getPlayer(), game.getTicketPrices(), result.getLeft());
    else
      return GuiRenderer.render(result.get().getPlayer(), result.get().getTicketPrices());
  }

  private String malformedCommand() {
    return "NYI";
  }
}
