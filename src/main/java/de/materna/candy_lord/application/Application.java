package de.materna.candy_lord.application;

import de.materna.candy_lord.api.GameAPI;
import de.materna.candy_lord.control.GameController;
import de.materna.candy_lord.control.IOController;
import de.materna.candy_lord.util.GuiRenderer;

import java.util.Locale;
import java.util.Scanner;

public class Application {
  public static void main(String[] args) {
    GameAPI game = GameAPI.create();
    IOController io = new IOController(game);
    Scanner scanner = new Scanner(System.in);
    System.out.println(GuiRenderer.render(game.newGame()));
    while (true) {
      String input = scanner.nextLine();
      if (input.trim().equalsIgnoreCase("stop")) break;
      System.out.println(io.parse(input));
    }
  }

}
