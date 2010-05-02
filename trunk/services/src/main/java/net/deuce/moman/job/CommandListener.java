package net.deuce.moman.job;

public interface CommandListener {
  public void commandStarted(Command command);
  public void commandFinished(Command command);
}
