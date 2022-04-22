package de.materna.candy_lord.util;

import de.materna.candy_lord.domain.CandyType;
import de.materna.candy_lord.dto.CityDTO;
import de.materna.candy_lord.dto.PlayerDTO;
import de.materna.candy_lord.dto.StateDTO;
import io.vavr.Tuple2;
import io.vavr.Tuple3;
import io.vavr.collection.List;

public class GuiRenderer {
  public static String render(StateDTO state) {
    //TODO custom message
    PlayerDTO player = state.player();
    CityDTO city = state.city();
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
        "| City: %20s            Maximum Capacity: %6d, Cash: %6d.%2d€ |",
        city.name(), player.maxCapacity(), cash._1, cash._2
    );

    String candyTrFormat = "| %-20s | %7d | %8d.%2d€ ";
    String ticketTrFormat = " %-20s: %6d.%2d€ |";
    var candyPrices = city.candyPrices();

    var ticketPriceTable = state.ticketPrices()
        .toList()
        .sortBy(Tuple2::_2)
        .reverse()
        .map(tuple -> tuple.map2(EuroRepresentation::of))
        .map(tuple -> String.format(
            ticketTrFormat,
            tuple._1,
            tuple._2.euro,
            tuple._2.cent
        ));

    var candyTable = List.of(CandyType.values()).map((type) ->
            new Tuple3<CandyType, Integer, Integer>(
                type,
                player.candies().get(type).get(),
                candyPrices.get(type).get()
            )
        )
        .sortBy(tuple -> -tuple._1.name().length()) // descending (notice -length)
        .map(tuple -> tuple.map3(EuroRepresentation::of))
        .map(tuple -> String.format(
            candyTrFormat,
            tuple._1,
            tuple._2,
            tuple._3.euro,
            tuple._3.cent
        ));

    var maxTableSize = Math.max(ticketPriceTable.length(), candyTable.length());

    //make tables same size
    var paddingElement = String.format(candyTrFormat, null, null, null, null);
    candyTable.padTo(maxTableSize, paddingElement);
    paddingElement = String.format(ticketTrFormat, null, null, null);
    ticketPriceTable.padTo(maxTableSize, paddingElement);

    var table = candyTable.zipWith(ticketPriceTable, (left, right) -> left + "|" + right);

    String legend = """
        | buy candy      : b candy-name amount                                             |
        | sell candy     : s candy-name amount                                             |
        | travel to city : t city-name                                                     |
        | undo turn      : undo                                                            |
        +----------------------------------------------------------------------------------+
        """;

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
    return lines.reduce((acc, elem) -> acc + "\n" + elem);

  }

  private static Tuple2<Integer, Integer> centToEuro(int cent) {
    return new Tuple2<>(cent / 100, Math.abs(cent % 100));
  }

  static class EuroRepresentation {
    public final int euro;
    public final int cent;

    private EuroRepresentation(int euro, int cent) {
      this.euro = euro;
      this.cent = cent;
    }

    public static EuroRepresentation of(int cent) {
      return new EuroRepresentation(cent / 100, Math.abs(cent % 100));
    }
  }
}
