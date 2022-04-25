package de.materna.candy_lord.application;

import de.materna.candy_lord.api.GameAPI;
import de.materna.candy_lord.control.IOController;
import de.materna.candy_lord.util.GuiRenderer;

import java.util.Scanner;

public class Application {
  public static void main(String[] args) {
    GameAPI game = GameAPI.create();
    IOController io = new IOController(game);
    Scanner scanner = new Scanner(System.in);
    playRound(game, scanner, io);
    while (true) {
      System.out.println("Want to play another Round? Type Y or N!");
      String input = scanner.nextLine().trim();
      if (input.equalsIgnoreCase("Y")) {
        playRound(game, scanner, io);
      } else if (input.equalsIgnoreCase("N"))
        System.out.println("Goodbye!");
        System.exit(0);
    }
  }

  public static void playRound(GameAPI game, Scanner scanner, IOController io) {
    System.out.println(GuiRenderer.render(game.newGame()));
    while (!game.isOver()) {
      String input = scanner.nextLine();
      if (input.trim().equalsIgnoreCase("stop")) return;
      System.out.println(io.parse(input));
    }
    System.out.println("Game Over! Look at your Score below:");
    System.out.println(game.getFinalScoreDescription().getOrElse(""));
  }
}
