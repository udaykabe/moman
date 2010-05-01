package net.deuce.moman.job;

import org.quartz.*;
import org.quartz.utils.Key;

import java.util.Date;

public class CommandWrappedTrigger extends Trigger {

  private SimpleTrigger trigger;
  private Command command;
  private String id;

  public CommandWrappedTrigger(String id, Command command) {
    this.trigger = (SimpleTrigger) TriggerUtils.makeImmediateTrigger(1, 0);
    this.id = id;
    this.command = command;
  }

  public String getId() {
    return id;
  }

  public Command getCommand() {
    return command;
  }

  public String getName() {
    return trigger.getName();
  }

  public void setName(String name) {
    trigger.setName(name);
  }

  public String getGroup() {
    return trigger.getGroup();
  }

  public void setGroup(String group) {
    trigger.setGroup(group);
  }

  public String getJobName() {
    return trigger.getJobName();
  }

  public void setJobName(String jobName) {
    trigger.setJobName(jobName);
  }

  public String getJobGroup() {
    return trigger.getJobGroup();
  }

  public void setJobGroup(String jobGroup) {
    trigger.setJobGroup(jobGroup);
  }

  public String getFullName() {
    return trigger.getFullName();
  }

  public Key getKey() {
    return trigger.getKey();
  }

  public String getFullJobName() {
    return trigger.getFullJobName();
  }

  public String getDescription() {
    return trigger.getDescription();
  }

  public void setDescription(String description) {
    trigger.setDescription(description);
  }

  public void setVolatility(boolean volatility) {
    trigger.setVolatility(volatility);
  }

  public void setCalendarName(String calendarName) {
    trigger.setCalendarName(calendarName);
  }

  public String getCalendarName() {
    return trigger.getCalendarName();
  }

  public JobDataMap getJobDataMap() {
    return trigger.getJobDataMap();
  }

  public void setJobDataMap(JobDataMap jobDataMap) {
    trigger.setJobDataMap(jobDataMap);
  }

  public boolean isVolatile() {
    return trigger.isVolatile();
  }

  public int getPriority() {
    return trigger.getPriority();
  }

  public void setPriority(int priority) {
    trigger.setPriority(priority);
  }

  public void addTriggerListener(String name) {
    trigger.addTriggerListener(name);
  }

  public boolean removeTriggerListener(String name) {
    return trigger.removeTriggerListener(name);
  }

  public String[] getTriggerListenerNames() {
    return trigger.getTriggerListenerNames();
  }

  public void clearAllTriggerListeners() {
    trigger.clearAllTriggerListeners();
  }

  public void triggered(Calendar calendar) {
    trigger.triggered(calendar);
  }

  public Date computeFirstFireTime(Calendar calendar) {
    return trigger.computeFirstFireTime(calendar);
  }

  public int executionComplete(JobExecutionContext jobExecutionContext, JobExecutionException e) {
    return trigger.executionComplete(jobExecutionContext, e);
  }

  public boolean mayFireAgain() {
    return trigger.mayFireAgain();
  }

  public Date getStartTime() {
    return trigger.getStartTime();
  }

  public void setStartTime(Date date) {
    trigger.setStartTime(date);
  }

  public void setEndTime(Date date) {
    trigger.setEndTime(date);
  }

  public Date getEndTime() {
    return trigger.getEndTime();
  }

  public Date getNextFireTime() {
    return trigger.getNextFireTime();
  }

  public Date getPreviousFireTime() {
    return trigger.getPreviousFireTime();
  }

  public Date getFireTimeAfter(Date date) {
    return trigger.getFireTimeAfter(date);
  }

  public Date getFinalFireTime() {
    return trigger.getFinalFireTime();
  }

  public void setMisfireInstruction(int misfireInstruction) {
    trigger.setMisfireInstruction(misfireInstruction);
  }

  protected boolean validateMisfireInstruction(int i) {
    return false;
  }

  public int getMisfireInstruction() {
    return trigger.getMisfireInstruction();
  }

  public void updateAfterMisfire(Calendar calendar) {
    trigger.updateAfterMisfire(calendar);
  }

  public void updateWithNewCalendar(Calendar calendar, long l) {
    trigger.updateWithNewCalendar(calendar, l);
  }

  public void validate() throws SchedulerException {
    trigger.validate();
  }

  public void setFireInstanceId(String id) {
    trigger.setFireInstanceId(id);
  }

  public String getFireInstanceId() {
    return trigger.getFireInstanceId();
  }

  public String toString() {
    return trigger.toString();
  }

  public int compareTo(Object obj) {
    return trigger.compareTo(obj);
  }

  public boolean equals(Object obj) {
    return trigger.equals(obj);
  }

  public int hashCode() {
    return trigger.hashCode();
  }

  public Object clone() {
    return trigger.clone();
  }
}