package net.deuce.moman.controller.command;

import javax.servlet.http.HttpServletResponse;

public class BaseResult {
  private Exception exception;
  private int responseCode = HttpServletResponse.SC_OK;
  private String message;

  public BaseResult() {
  }

  public BaseResult(int responseCode, Exception exception, String message) {
    this.exception = exception;
    this.message = message;
    this.responseCode = responseCode;
  }

  public Exception getException() {
    return exception;
  }

  public void setException(Exception exception) {
    this.exception = exception;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public int getResponseCode() {
    return responseCode;
  }

  public void setResponseCode(int responseCode) {
    this.responseCode = responseCode;
  }

}