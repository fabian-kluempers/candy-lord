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
    // template
    String s = """
        +----------------------------------------------------------------------------------+
        |                                   Candy Lord                                     |
        +----------------------------------------------------------------------------------+
        | City: aaaabbbbccccddddeeee                  maximum capacity: 120, cash: 613.13€ |
        +----------------------+---------+--------------+----------------------------------+
        | Candies              | On Hand | Street Price | Ticket Prices                    |
        +----------------------+---------+--------------+----------------------------------+
        | aaaabbbbccccddddeeee | aaabbbb | eeeeeeee.cc€ | aaaabbbbccccddddeeee: eeeeee.cc€ |
        | Marshmallow             |       2 |       31.29€ | Berlin               123.42€ |
        | Lemonade                |       1 |        5.04€ | etc                          |
        | Cake                  |      22 |        0.45€ |                              |
        +-----------------------+--------+--------------+---------------------------------+
        | buy candy      : b candy-name amount                                            |
        | sell candy     : s candy-name amount                                            |
        | travel to city : t city-name                                                    |
        | undo turn      : undo                                                           |
        +---------------------------------------------------------------------------------+
        """;

    Tuple2<Integer, Integer> cash = centToEuro(player.cash());

    String header = String.format(
        "| City: %-20s   Day: %2d, Maximum Capacity: %6d, Cash: %6d.%02d€ |",
        city.name(), state.day(), player.maxCapacity(), cash._1, cash._2
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
    var paddingElement = String.format(candyTrFormat, null, null, null, null, null);
    candyTable.padTo(maxTableSize, paddingElement);
    paddingElement = String.format(ticketTrFormat, null, null, null, null);
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
      String paddedMessage = new StringBuilder().insert(0, padding).append(message).append(padding).toString();
      lines = lines
          .append(String.format("|%82s|", paddedMessage))
          .append("+----------------------------------------------------------------------------------+");
    }

    return lines.reduce((acc, elem) -> acc + "\n" + elem);

  }

  private static Tuple2<Integer, Integer> centToEuro(int cent) {
    return new Tuple2<>(cent / 100, Math.abs(cent % 100));
  }
}
