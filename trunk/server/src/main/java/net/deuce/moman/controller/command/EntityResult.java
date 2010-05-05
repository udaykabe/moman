package net.deuce.moman.controller.command;

public class EntityResult extends BaseResult {
  private Object value;

  public EntityResult() {
  }

  public EntityResult(Object value) {
    super();
    this.value = value;
  }

  public EntityResult(int responseCode, Exception exception, String message) {
    super(responseCode, exception, message);
  }

  public Object getValue() {
    return value;
  }

  public void setValue(Object value) {
    this.value = value;
  }
}