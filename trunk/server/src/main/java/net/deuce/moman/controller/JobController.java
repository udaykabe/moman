package net.deuce.moman.controller;

import net.deuce.moman.controller.command.CommandResult;
import net.deuce.moman.job.Command;
import net.deuce.moman.job.CommandListener;
import net.deuce.moman.job.UndoManager;
import net.deuce.moman.om.AllocationSetService;
import net.deuce.moman.om.EntityService;
import net.sf.ehcache.Cache;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

public class JobController extends DispatcherController implements InitializingBean, CommandListener {

  private Logger logger = LoggerFactory.getLogger(getClass());

  @Autowired
  private AllocationSetService allocationSetService;

  @Autowired
  private UndoManager undoManager;

  private Cache cache;

  protected EntityService getService() {
    return null;
  }

  public Cache getCache() {
    return cache;
  }

  public void setCache(Cache cache) {
    this.cache = cache;
  }

  public void afterPropertiesSet() throws Exception {
    undoManager.addCommandListener(this);
  }

  public void commandStarted(Command command) {
    cache.put(new net.sf.ehcache.Element(command.getId(), new CommandResult(JobStatus.STARTED, null)));
  }

  public void commandFinished(Command command) {
    CommandResult result;
    if (command.getException() != null) {
      logger.error("Command (" + command.getId() + ") failed", command.getException());
      Element error = DocumentHelper.createElement("error");
      error.addElement("message").setText(command.getException().getMessage());
      result = new CommandResult(JobStatus.ERROR, error);
    } else {
      result = new CommandResult(JobStatus.COMPLETED, command.getResult());
    }
    cache.put(new net.sf.ehcache.Element(command.getId(), result));
  }

}