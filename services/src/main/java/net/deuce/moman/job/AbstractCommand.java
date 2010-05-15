package net.deuce.moman.job;

import org.dom4j.Element;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

public abstract class AbstractCommand implements Command {

  private String id;
  private String name;
  private boolean immediate;
  private List<Element> result;
  private Command undoCommand;
  private int resultCode = HttpServletResponse.SC_OK;
  private boolean running = false;
  private Exception exception;

  protected AbstractCommand(String name, boolean immediate) {
    this.name = name;
    this.immediate = immediate;
  }

  public Exception getException() {
    return exception;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public boolean isImmedidate() {
    return immediate;
  }

  public List<Element> getResult() {
    return result;
  }

  public void setResult(List<Element> result) {
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

  public boolean isRunning() {
    return running;
  }

  public void run() {
    running = true;

    try {
      doExecute();
    } catch (Exception e) {
      exception = e;
    }
    running = false;
  }
}
