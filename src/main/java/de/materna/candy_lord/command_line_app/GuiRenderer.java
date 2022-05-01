package de.materna.candy_lord.command_line_app;

import de.materna.candy_lord.core.dto.CityDTO;
import de.materna.candy_lord.core.dto.EuroRepresentation;
import de.materna.candy_lord.core.dto.PlayerDTO;
import de.materna.candy_lord.core.dto.StateDTO;
import io.vavr.collection.List;
import io.vavr.collection.Map;
import io.vavr.collection.Set;
import io.vavr.control.Option;
import io.vavr.control.Try;

public class GuiRenderer {
  public static String render(StateDTO state, Map<String, Integer> candyIndices, Map<String, Integer> cityIndices, Set<String> candyNames) {
    PlayerDTO player = state.player();
    CityDTO city = state.city();

    String header = String.format(
        "| City: %-20s   Day: %2d, Maximum Capacity: %6d, Cash: %6d.%02d€ |",
        city.name(), state.day(), player.maxCapacity(), player.cash().euro, player.cash().cent
    );

    String candyTrFormat = "| [%1d] %-16s | %7d | %8d.%02d€ ";
    String ticketTrFormat = " [%1d] %-16s: %6d.%02d€ |";

    List<String> table = createTable(
        createTicketPriceTable(ticketTrFormat, cityIndices, state.ticketPrices()),
        createCandyTable(candyTrFormat, candyIndices, player.candies(), city.candyPrices())
    );

    String legend = """
        | buy candy      : b candy-index amount                                            |
        | sell candy     : s candy-index amount                                            |
        | travel to city : t city-index                                                    |
        | undo turn      : undo                                                            |
        | exit game      : exit                                                            |
        +----------------------------------------------------------------------------------+""";

    return List
        .of("+----------------------------------------------------------------------------------+")
        .append("|                                   Candy Lord                                     |")
        .append("+----------------------------------------------------------------------------------+")
        .append(header)
        .append("+----------------------+---------+--------------+----------------------------------+")
        .append("| Candies              | On Hand | Street Price | Ticket Prices                    |")
        .append("+----------------------+---------+--------------+----------------------------------+")
        .appendAll(table)
        .append("+----------------------+---------+--------------+----------------------------------+")
        .append(legend)
        .appendAll(formatMessage(state.message()))
        .reduce((acc, elem) -> acc + "\n" + elem);
  }

  private static List<String> formatMessage(Option<String> message) {
    return message.map(msg ->
        List.of(
            String.format(
                "|%-82s|",
                Try.of(() -> " ".repeat((82 - msg.length()) / 2)).getOrElse("") + msg
            ),
            "+----------------------------------------------------------------------------------+"
        )
    ).getOrElse(List.empty());
  }

  private static List<String> createTicketPriceTable(
      String ticketTrFormat,
      Map<String, Integer> cityIndices,
      Map<String, EuroRepresentation> ticketPrices
  ) {
    return ticketPrices
        .toList()
        .sortBy(entry -> -entry._1.length()) // descending (notice -length)
        .map(tuple -> String.format(
            ticketTrFormat,
            cityIndices.get(tuple._1).get(), // index
            tuple._1, // name
            tuple._2.euro, // price
            tuple._2.cent // price
        ));
  }

  private static List<String> createCandyTable(
      String candyTrFormat,
      Map<String, Integer> candyIndices,
      Map<String, Integer> candies,
      Map<String, EuroRepresentation> candyPrices
  ) {
    return candyIndices.toList()
        .sortBy(entry -> -entry._1.length()) // descending (notice -length)
        .map(entry -> String.format(
                candyTrFormat,
                entry._2, // index
                entry._1, // name
                candies.get(entry._1).get(), // on hand
                candyPrices.get(entry._1).get().euro, // price
                candyPrices.get(entry._1).get().cent  // price
            )
        );
  }

  private static List<String> createTable(List<String> ticketPriceTable, List<String> candyTable) {
    int maxTableSize = Math.max(ticketPriceTable.length(), candyTable.length());

    String candyPad = String.format("|%22s|%9s|%14s", "", "", "");
    candyTable = candyTable.padTo(maxTableSize, candyPad);
    String ticketPad = " ".repeat(34) + "|";
    ticketPriceTable = ticketPriceTable.padTo(maxTableSize, ticketPad);

    return candyTable.zipWith(ticketPriceTable, (left, right) -> left + "|" + right);
  }
}
