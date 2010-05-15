package net.deuce.moman.job;

import net.deuce.moman.om.User;
import net.deuce.moman.util.Utils;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class UndoManager implements InitializingBean, Runnable {

  private Logger logger = LoggerFactory.getLogger(getClass());

  private Map<User, Stack<CommandHolder>> undoStackMap = new HashMap<User, Stack<CommandHolder>>();
  private Map<User, Stack<CommandHolder>> redoStackMap = new HashMap<User, Stack<CommandHolder>>();

  private List<Command> activeCommands = new LinkedList<Command>();

  private Set<CommandListener> commandListeners = new HashSet<CommandListener>();

  private boolean running = true;

  public void shutdown() {
    running = false;
  }

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
    List<Element> result = doExecute(user, command);
    if (undoCommand == null) {
      undoCommand = command.getUndo();
    }
    if (undoCommand != null) {
      CommandHolder holder = new CommandHolder(undoCommand, command);
      getUndoStack(user).push(holder);
    }
    return new Result(command.getResultCode(), result);
  }

  private List<Element> buildResponse(User user, Element result) {
    Element response = DocumentHelper.createElement("job")
        .addAttribute("undo-count", Integer.toString(getUndoStack(user).size()))
        .addAttribute("redo-count", Integer.toString(getRedoStack(user).size()));
    response.add(result);
    return Arrays.asList(new Element[]{response});
  }

  private List<Element> doExecute(User user, Command command) throws Exception {
    if (command.isImmedidate()) {
      command.doExecute();
      return command.getResult();
    }

    String jobId = queue(command);
    return buildResponse(user, DocumentHelper.createElement("job").addAttribute("id", jobId));
  }

  private String queue(Command command) throws SchedulerException {
    String id = Utils.createUuid();
    command.setId(id);

    Thread t = new Thread(command);
    activeCommands.add(command);
    t.start();
    notifyCommandStarted(command);
    return id;
  }

  public List<Element> undo(User user) throws Exception {
    Stack<CommandHolder> stack = getUndoStack(user);
    if (stack.size() == 0) return null;

    CommandHolder holder = stack.pop();
    getRedoStack(user).push(holder);
    return doExecute(user, holder.undo);
  }

  public List<Element> redo(User user) throws Exception {
    Stack<CommandHolder> stack = getRedoStack(user);
    if (stack.size() == 0) return null;

    CommandHolder holder = stack.pop();
    getUndoStack(user).push(holder);
    return doExecute(user, holder.redo);
  }

  protected void notifyCommandStarted(Command command) {
    for (CommandListener listener : commandListeners) {
      listener.commandStarted(command);
    }
  }

  protected void notifyCommandFinished(Command command) {
    for (CommandListener listener : commandListeners) {
      listener.commandFinished(command);
    }
  }

  public void addCommandListener(CommandListener commandListener) {
    commandListeners.add(commandListener);
  }

  public void removeCommandListener(CommandListener commandListener) throws SchedulerException {
    commandListeners.remove(commandListener);
  }

  public void run() {
    try {
      while (running) {
        ListIterator<Command> itr = activeCommands.listIterator();
        while (itr.hasNext()) {
          Command command = itr.next();
          if (!command.isRunning()) {
            itr.remove();
            notifyCommandFinished(command);
          }
        }
        Thread.sleep(1000);
      }
    } catch (InterruptedException e) {
      logger.error("UndoManager interrupted", e);
    }
  }

  public void afterPropertiesSet() throws Exception {
    Thread t = new Thread(this);
    t.setDaemon(true);
    t.start();
  }

  private static class CommandHolder {
    public Command undo;
    public Command redo;

    private CommandHolder(Command undo, Command redo) {
      this.undo = undo;
      this.redo = redo;
    }
  }

}
