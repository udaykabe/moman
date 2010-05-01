package net.deuce.moman.job;

import org.dom4j.Element;

public class Result {

  private int resultCode;
  private Element result;

  public Result(int resultCode, Element result) {
    this.resultCode = resultCode;
    this.result = result;
  }

  public int getResultCode() {
    return resultCode;
  }

  public Element getResult() {
    return result;
  }
}
