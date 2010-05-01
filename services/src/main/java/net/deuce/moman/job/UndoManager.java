package net.deuce.moman.job;

import net.deuce.moman.om.User;
import net.deuce.moman.util.Utils;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.quartz.*;
import org.quartz.spi.JobFactory;
import org.quartz.spi.TriggerFiredBundle;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

@Service
public class UndoManager implements InitializingBean {

  private Map<User, Stack<CommandHolder>> undoStackMap = new HashMap<User, Stack<CommandHolder>>();
  private Map<User, Stack<CommandHolder>> redoStackMap = new HashMap<User, Stack<CommandHolder>>();
  private Scheduler scheduler;

  private Stack<CommandHolder> getUndoStack(User user) {
    Stack<CommandHolder> stack = undoStackMap.get(user);
    if (stack == null) {
      stack = new Stack<CommandHolder>();
      undoStackMap.put(user, stack);
    }
    return stack;
  }

  private Stack<CommandHolder> getRedoStack(User user) {
    Stack<CommandHolder> stack = redoStackMap.get(user);
    if (stack == null) {
      stack = new Stack<CommandHolder>();
      redoStackMap.put(user, stack);
    }
    return stack;
  }

  public Result execute(User user, Command command, Command undoCommand) throws Exception {
    Element result = doExecute(user, command);
    if (undoCommand == null) {
      undoCommand = command.getUndo();
    }
    if (undoCommand != null) {
      CommandHolder holder = new CommandHolder(undoCommand, command);
      getUndoStack(user).push(holder);
    }
    return new Result(command.getResultCode(), result);
  }

  private Element buildResponse(User user, Element result) {
    Element response = DocumentHelper.createElement("job")
        .addAttribute("undo-count", Integer.toString(getUndoStack(user).size()))
        .addAttribute("redo-count", Integer.toString(getRedoStack(user).size()));
    response.add(result);
    return response;
  }

  private Element doExecute(User user, Command command) throws Exception {
    if (command.isImmedidate()) {
      command.doExecute();
      return command.getResult();
    }

    String jobId = schedule(command);
    return buildResponse(user, DocumentHelper.createElement("job").addAttribute("id", jobId));
  }

  private String schedule(Command command) throws SchedulerException {
    CommandWrappedTrigger trigger = new CommandWrappedTrigger(Utils.createUuid(), command);
    JobDetail jobDetail = new JobDetail(command.getName(), scheduler.DEFAULT_GROUP, Command.class);
    scheduler.scheduleJob(jobDetail, trigger);
    return trigger.getId();
  }

  public Element undo(User user) throws Exception {
    Stack<CommandHolder> stack = getUndoStack(user);
    if (stack.size() == 0) return null;

    CommandHolder holder = stack.pop();
    getRedoStack(user).push(holder);
    return doExecute(user, holder.undo);
  }

  public Element redo(User user) throws Exception {
    Stack<CommandHolder> stack = getRedoStack(user);
    if (stack.size() == 0) return null;

    CommandHolder holder = stack.pop();
    getUndoStack(user).push(holder);
    return doExecute(user, holder.redo);
  }

  public void addTriggerListener(TriggerListener triggerListener) throws SchedulerException {
    scheduler.addTriggerListener(triggerListener);
  }

  public void removeTriggerListener(TriggerListener triggerListener) throws SchedulerException {
    scheduler.removeTriggerListener(triggerListener.getName());
  }

  public void afterPropertiesSet() throws Exception {
    SchedulerFactory schedFact = new org.quartz.impl.StdSchedulerFactory();
    scheduler = schedFact.getScheduler();
    scheduler.setJobFactory(new QuickJobFactory());
    scheduler.start();
  }

  private static class CommandHolder {
    public Command undo;
    public Command redo;

    private CommandHolder(Command undo, Command redo) {
      this.undo = undo;
      this.redo = redo;
    }
  }

  private static class QuickJobFactory implements JobFactory {

    public Job newJob(TriggerFiredBundle triggerFiredBundle) throws SchedulerException {
      CommandWrappedTrigger trigger = (CommandWrappedTrigger) triggerFiredBundle.getTrigger();
      return trigger.getCommand();
    }
  }
}
