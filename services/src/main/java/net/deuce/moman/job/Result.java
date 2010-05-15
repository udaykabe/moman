package net.deuce.moman.job;

import org.dom4j.Element;

import java.util.List;

public class Result {

  private int resultCode;
  private List<Element> result;

  public Result(int resultCode, List<Element> result) {
    this.resultCode = resultCode;
    this.result = result;
  }

  public int getResultCode() {
    return resultCode;
  }

  public List<Element> getResult() {
    return result;
  }
}
