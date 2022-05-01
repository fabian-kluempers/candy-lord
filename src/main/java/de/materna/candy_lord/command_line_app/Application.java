package de.materna.candy_lord.command_line_app;

import de.materna.candy_lord.core.api.GameAPI;

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
      } else if (input.equalsIgnoreCase("N")) {
        System.out.println("Goodbye!");
        System.exit(0);
      }
    }
  }

  public static void playRound(GameAPI game, Scanner scanner, IOController io) {
    System.out.println(io.renderState(game.newGame()));
    while (game.isNotOver()) {
      String input = scanner.nextLine();
      if (input.trim().equalsIgnoreCase("exit")) {
        break;
      }
      System.out.println(io.parse(input));
    }
    System.out.println("Game Over! Look at your Score below:");
    System.out.println(
        game.getFinalScore()
            .map(score -> String.format(
                "Your final cash amount after selling all candies is: %d.%2d€",
                score.euro,
                score.cent
            ))
            .getOrElse("")
    );
  }
}
