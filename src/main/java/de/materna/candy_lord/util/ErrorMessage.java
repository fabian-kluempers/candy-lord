package de.materna.candy_lord.util;

import java.util.Objects;

public class ErrorMessage {
  private final String message;

  public ErrorMessage(String message) {
    this.message = Objects.requireNonNull(message);
  }
}
