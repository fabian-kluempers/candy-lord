package de.materna.candy_lord.util;

import de.materna.candy_lord.domain.CandyType;
import de.materna.candy_lord.dto.CityDTO;
import de.materna.candy_lord.dto.PlayerDTO;
import de.materna.candy_lord.dto.StateDTO;
import io.vavr.Tuple2;
import io.vavr.Tuple3;
import io.vavr.collection.List;
import io.vavr.collection.Map;
import io.vavr.control.Try;

public class GuiRenderer {
  public static String render(StateDTO state, Map<CandyType, Integer> candyIndices, Map<String, Integer> cityIndices) {
    PlayerDTO player = state.player();
    CityDTO city = state.city();

    EuroRepresentation cash = EuroRepresentation.of(player.cash());

    String header = String.format(
        "| City: %-20s   Day: %2d, Maximum Capacity: %6d, Cash: %6d.%02d€ |",
        city.name(), state.day(), player.maxCapacity(), cash.euro, cash.cent
    );

    String candyTrFormat = "| [%d] %-16s | %7d | %8d.%02d€ ";
    String ticketTrFormat = " [%d] %-16s: %6d.%02d€ |";
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

    var candyTable = List.of(CandyType.values()).map((type) ->
            new Tuple3<>(
                type,
                player.candies().get(type).get(),
                candyPrices.get(type).get()
            )
        )
        .sortBy(tuple -> -tuple._1.name().length()) // descending (notice -length)
        .map(tuple -> tuple.map3(EuroRepresentation::of))
        .map(tuple -> String.format(
            candyTrFormat,
            candyIndices.get(tuple._1).get(),
            tuple._1,
            tuple._2,
            tuple._3.euro,
            tuple._3.cent
        ));

    var maxTableSize = Math.max(ticketPriceTable.length(), candyTable.length());

    //make tables same size
    var paddingElement = String.format(candyTrFormat, new Object[5]);
    candyTable.padTo(maxTableSize, paddingElement);
    paddingElement = String.format(ticketTrFormat, new Object[4]);
    ticketPriceTable.padTo(maxTableSize, paddingElement);

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
      String padding = Try.of(() -> " ".repeat((82 - message.length())/2)).getOrElse("");
      String paddedMessage = message + padding;
      lines = lines
          .append(String.format("|%82s|", paddedMessage))
          .append("+----------------------------------------------------------------------------------+");
    }

    return lines.reduce((acc, elem) -> acc + "\n" + elem);

  }
}
