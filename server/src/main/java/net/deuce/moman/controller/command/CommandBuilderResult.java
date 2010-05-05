package net.deuce.moman.controller.command;

import net.deuce.moman.job.Command;

import javax.servlet.http.HttpServletResponse;

public class CommandBuilderResult {

  private Command command;
  private Exception exception;
  private String message;
  private int responseCode = HttpServletResponse.SC_OK;

  public CommandBuilderResult(Command command) {
    this.command = command;
  }

  public CommandBuilderResult(int responseCode, Exception exception, String message) {
    this.responseCode = responseCode;
    this.exception = exception;
    this.message = message;
  }

  public Command getCommand() {
    return command;
  }

  public Exception getException() {
    return exception;
  }

  public String getMessage() {
    return message;
  }

  public int getResponseCode() {
    return responseCode;
  }
}
