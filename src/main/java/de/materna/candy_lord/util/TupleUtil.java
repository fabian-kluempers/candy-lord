package de.materna.candy_lord.util;

import io.vavr.Tuple2;

public class TupleUtil {
  public static <K, V> Tuple2<K, V> flip(Tuple2<V, K> tuple) {
    return new Tuple2<>(tuple._2, tuple._1);
  }
}
