package de.materna.candy_lord.util;

import de.materna.candy_lord.domain.CandyType;
import de.materna.candy_lord.dto.CityDTO;
import de.materna.candy_lord.dto.PlayerDTO;
import de.materna.candy_lord.dto.StateDTO;
import io.vavr.Tuple3;
import io.vavr.collection.List;
import io.vavr.collection.Map;
import io.vavr.collection.Set;
import io.vavr.control.Try;

public class GuiRenderer {
  public static String render(StateDTO state, Map<String, Integer> candyIndices, Map<String, Integer> cityIndices, Set<String> candyNames) {
    PlayerDTO player = state.player();
    CityDTO city = state.city();

    EuroRepresentation cash = EuroRepresentation.of(player.cash());

    String header = String.format(
        "| City: %-20s   Day: %2d, Maximum Capacity: %6d, Cash: %6d.%02d€ |",
        city.name(), state.day(), player.maxCapacity(), cash.euro, cash.cent
    );

    String candyTrFormat = "| [%1d] %-16s | %7d | %8d.%02d€ ";
    String ticketTrFormat = " [%1d] %-16s: %6d.%02d€ |";
    var candyPrices = city.candyPrices();

    var ticketPriceTable = state.ticketPrices()
        .toList()
        .sortBy(entry -> -entry._1.length()) // descending (notice -length)
        .map(tuple -> tuple.map2(EuroRepresentation::of))
        .map(tuple -> String.format(
            ticketTrFormat,
            cityIndices.get(tuple._1).get(),
            tuple._1,
            tuple._2.euro,
            tuple._2.cent
        ));

    var candyTable = candyNames.toList()
        .sortBy(String::length)
        .reverse()
        .map(candy -> {
              var euroRep = EuroRepresentation.of(candyPrices.get(candy).get());
              return String.format(
                  candyTrFormat,
                  candyIndices.get(candy).get(),
                  candy,
                  player.candies().get(candy).get(),
                  euroRep.euro,
                  euroRep.cent
              );
            }
        );

    var maxTableSize = Math.max(ticketPriceTable.length(), candyTable.length());

    //make tables same size

    String candyPad = String.format("|%22s|%9s|%14s", "", "", "");
    candyTable = candyTable.padTo(maxTableSize, candyPad);
    String ticketPad = " ".repeat(34) + "|";
    ticketPriceTable = ticketPriceTable.padTo(maxTableSize, ticketPad);

    var table = candyTable.zipWith(ticketPriceTable, (left, right) -> left + "|" + right);

    String legend = """
        | buy candy      : b candy-index amount                                            |
        | sell candy     : s candy-index amount                                            |
        | travel to city : t city-index                                                    |
        | undo turn      : undo                                                            |
        +----------------------------------------------------------------------------------+""";

    List<String> lines = List
        .of("+----------------------------------------------------------------------------------+")
        .append("|                                   Candy Lord                                     |")
        .append("+----------------------------------------------------------------------------------+")
        .append(header)
        .append("+----------------------+---------+--------------+----------------------------------+")
        .append("| Candies              | On Hand | Street Price | Ticket Prices                    |")
        .append("+----------------------+---------+--------------+----------------------------------+")
        .appendAll(table)
        .append("+----------------------+---------+--------------+----------------------------------+")
        .append(legend);


    // insert centered message
    if (state.message().isDefined()) {
      String message = state.message().get();
      String padding = Try.of(() -> " ".repeat((82 - message.length()) / 2)).getOrElse("");
      String paddedMessage = message + padding;
      lines = lines
          .append(String.format("|%82s|", paddedMessage))
          .append("+----------------------------------------------------------------------------------+");
    }

    return lines.reduce((acc, elem) -> acc + "\n" + elem);

  }
}
