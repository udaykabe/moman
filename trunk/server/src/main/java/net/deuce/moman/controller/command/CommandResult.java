package net.deuce.moman.controller.command;

import net.deuce.moman.controller.JobStatus;
import org.dom4j.Element;

import java.io.Serializable;

public class CommandResult implements Serializable {
  public JobStatus jobStatus;
  public Element result;

  public CommandResult(JobStatus jobStatus, Element result) {
    this.jobStatus = jobStatus;
    this.result = result;
  }

  public JobStatus getJobStatus() {
    return jobStatus;
  }

  public Element getResult() {
    return result;
  }
}
