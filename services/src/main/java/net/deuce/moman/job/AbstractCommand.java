package net.deuce.moman.job;

import org.dom4j.Element;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public abstract class AbstractCommand implements Command {

  private String name;
  private boolean immediate;
  private Element result;
  private Command undoCommand;
  private int resultCode;

  protected AbstractCommand(String name, boolean immediate) {
    this.name = name;
    this.immediate = immediate;
  }

  public String getName() {
    return name;
  }

  public boolean isImmedidate() {
    return immediate;
  }

  public Element getResult() {
    return result;
  }

  public void setResult(Element result) {
    this.result = result;
  }

  public int getResultCode() {
    return resultCode;
  }

  public void setResultCode(int resultCode) {
    this.resultCode = resultCode;
  }

  public abstract void doExecute() throws Exception;

  public Command getUndo() {
    return undoCommand;
  }

  public void setUndo(Command undoCommand) {
    this.undoCommand = undoCommand;
  }

  public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
    try {
      doExecute();
    } catch (Exception e) {
      throw new JobExecutionException(e);
    }
  }
}
