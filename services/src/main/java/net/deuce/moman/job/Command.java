package net.deuce.moman.job;

import org.dom4j.Element;

import java.util.List;

public interface Command extends Runnable {
  public String getId();
  public void setId(String id);
  public String getName();
  public boolean isImmedidate();
  public List<Element> getResult();
  public void doExecute() throws Exception;
  public Command getUndo();
  public int getResultCode();
  public Exception getException();
  public boolean isRunning();
}
