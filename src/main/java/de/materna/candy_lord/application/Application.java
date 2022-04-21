package de.materna.candy_lord.application;

import de.materna.candy_lord.domain.City;
import de.materna.candy_lord.domain.Player;
import de.materna.candy_lord.logic.Game;
import de.materna.candy_lord.logic.IOController;
import de.materna.candy_lord.util.GuiRenderer;
import io.vavr.Tuple2;
import io.vavr.collection.List;
import io.vavr.collection.Map;

import java.awt.*;
import java.util.Scanner;

public class Application {


  public static void main(String[] args) {
    Game game = new Game();
    IOController io = new IOController(game);
    Scanner scanner = new Scanner(System.in);
    System.out.println(GuiRenderer.render(game.getPlayer(), game.getTicketPrices()));
    while (true) {
      String input = scanner.nextLine();
      System.out.println(io.parse(input));
    }
  }

}
