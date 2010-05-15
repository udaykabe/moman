package net.deuce.moman.controller.command;

import net.deuce.moman.controller.JobStatus;
import org.dom4j.Element;

import java.io.Serializable;
import java.util.List;

public class CommandResult implements Serializable {
  public JobStatus jobStatus;
  public List<Element> result;

  public CommandResult(JobStatus jobStatus, List<Element> result) {
    this.jobStatus = jobStatus;
    this.result = result;
  }

  public JobStatus getJobStatus() {
    return jobStatus;
  }

  public List<Element> getResult() {
    return result;
  }
}
