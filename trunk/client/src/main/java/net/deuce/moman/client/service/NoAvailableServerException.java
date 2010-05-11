package net.deuce.moman.client.service;

public class NoAvailableServerException extends Exception {
  public NoAvailableServerException() {
  }

  public NoAvailableServerException(Throwable cause) {
    super(cause);
  }

  public NoAvailableServerException(String message) {
    super(message);
  }

  public NoAvailableServerException(String message, Throwable cause) {
    super(message, cause);
  }
}
