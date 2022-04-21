package de.materna.candy_lord.util;

import de.materna.candy_lord.domain.CandyType;
import de.materna.candy_lord.domain.Player;
import io.vavr.Tuple2;
import io.vavr.Tuple3;
import io.vavr.collection.List;
import io.vavr.collection.Map;

public class GuiRenderer {
  public static String render(Player player, Map<String, Integer> ticketPrices, String customMessage) {
    return render(player, ticketPrices) + "\n" + customMessage;
  }

  public static String render(Player player, Map<String, Integer> ticketPrices) {
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
        +-----------------------+---------+--------------+------------------------------+
        | buy candy      : b candy-name amount                                           |
        | sell candy     : s candy-name amount                                           |
        | travel to city : t city-name                                                   |
        | undo turn      : undo                                                           |
        +---------------------------------------------------------------------------------+
        """;

    Tuple2<Integer, Integer> cash = centToEuro(player.cash());

    String header = String.format(
        "| City: %20s            Maximum Capacity: %6d, Cash: %6d.%2d€ |",
        player.city().name(), player.maxCapacity(), cash._1, cash._2
    );

    String candyTrFormat = "| %-20s | %7d | %8d.%2d€ ";
    String ticketTrFormat = " %-20s: %6d.%2d€ |";
    var candyPrices = player.city().candyPrices();

    var ticketPriceTable = ticketPrices
        .toList()
        .sortBy(Tuple2::_2)
        .reverse()
        .map(tuple -> String.format(
            ticketTrFormat,
            tuple._1,
            centToEuro(tuple._2)._1,
            centToEuro(tuple._2)._2
        ));

    var candyTable = List.of(CandyType.values()).map((type) ->
            new Tuple3<CandyType, Integer, Integer>(
                type,
                player.candies().get(type).get(),
                candyPrices.get(type).get()
            )
        )
        .sortBy(tuple -> -tuple._1.name().length()) // descending (notice -length)
        .map(tuple -> String.format(
            candyTrFormat,
            tuple._1,
            tuple._2,
            centToEuro(tuple._3)._1,
            centToEuro(tuple._3)._2
        ));

    var maxTableSize = Math.max(ticketPriceTable.length(), candyTable.length());

    //make tables same size
    var paddingElement = String.format(candyTrFormat, null, null, null, null);
    candyTable.padTo(maxTableSize, paddingElement);
    paddingElement = String.format(ticketTrFormat, null, null, null);
    ticketPriceTable.padTo(maxTableSize, paddingElement);

    var table = candyTable.zipWith(ticketPriceTable, (left, right) -> left + "|" + right);


    List<String> lines = List
        .of("+----------------------------------------------------------------------------------+")
        .append("|                                   Candy Lord                                     |")
        .append("+----------------------------------------------------------------------------------+")
        .append(header)
        .append("+----------------------+---------+--------------+----------------------------------+")
        .append("| Candies              | On Hand | Street Price | Ticket Prices                    |")
        .append("+----------------------+---------+--------------+----------------------------------+")
        .appendAll(table)
        .append("+----------------------+---------+--------------+----------------------------------+");
    return lines.foldLeft("", (acc, elem) -> acc + "\n" + elem);

  }

  private static Tuple2<Integer, Integer> centToEuro(int cent) {
    return new Tuple2<>(cent / 100, Math.abs(cent % 100));
  }
}
