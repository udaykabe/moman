package net.deuce.moman.job;

import org.dom4j.Element;
import org.quartz.Job;

public interface Command extends Job {
  public String getName();
  public boolean isImmedidate();
  public Element getResult();
  public void doExecute() throws Exception;
  public Command getUndo();
  public int getResultCode();
}
