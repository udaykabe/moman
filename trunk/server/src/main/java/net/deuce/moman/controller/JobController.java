package net.deuce.moman.controller;

import net.deuce.moman.job.Command;
import net.deuce.moman.job.CommandListener;
import net.deuce.moman.job.Result;
import net.deuce.moman.om.AllocationSetService;
import net.deuce.moman.om.EntityService;
import net.deuce.moman.om.User;
import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

public class JobController extends AbstractController implements InitializingBean, CommandListener {

  private Logger logger = LoggerFactory.getLogger(getClass());

  private static enum JobStatus {
    NONE, STARTED, COMPLETED, ERROR
  }

  @Autowired
  private AllocationSetService allocationSetService;

  private CacheManager cacheManager;
  private Cache jobStatusCache;

  protected EntityService getService() {
    return null;
  }

  public ModelAndView handleRequest(HttpServletRequest req, HttpServletResponse res) throws Exception {

    Parameter uuid;
    Parameter action = new Parameter("action", Integer.class);
    List<Parameter> params = new LinkedList<Parameter>();
    params.add(action);
    Result result;

    if (!checkParameters(req, res, params)) return null;

    switch (action.getIntValue()) {
      case Actions.JOB_STATUS:
        uuid = new Parameter("uuid", String.class);
        params.add(uuid);
        if (!checkParameters(req, res, params)) return null;

        checkJobStatus(uuid.getValue(), req, res);
        break;
      case Actions.UNDO_COMMAND:
        result = undo();
        if (result != null && result.getResult() != null) {
          sendResult(result, res);
        } else {
          res.setStatus(HttpServletResponse.SC_NOT_FOUND);
        }
        break;
      case Actions.REDO_COMMAND:
        result = redo();
        if (result != null && result.getResult() != null) {
          sendResult(result, res);
        } else {
          res.setStatus(HttpServletResponse.SC_NOT_FOUND);
        }
        break;
    }

    return null;
  }

  private Result undo() throws Exception {
    User user = getUserService().getStaticUser();
    return new Result(HttpServletResponse.SC_OK, getUndoManager().undo(user));
  }

  private Result redo() throws Exception {
    User user = getUserService().getStaticUser();
    return new Result(HttpServletResponse.SC_OK, getUndoManager().redo(user));
  }

  protected void checkJobStatus(String uuid, HttpServletRequest req, HttpServletResponse res) throws IOException {
    net.sf.ehcache.Element element = jobStatusCache.get(uuid);
    JobStatus status = JobStatus.NONE;
    Element result = null;
    if (element != null) {
      status = ((CommandResult) element.getValue()).jobStatus;
      result = ((CommandResult) element.getValue()).result;
    }

    Document doc = buildResponse();
    Element root = doc.getRootElement()
        .addElement("job-status")
        .addAttribute("uuid", uuid)
        .addAttribute("status", status.name());
    if (result != null) {
      root.add(result);
    }
    sendResponse(res, doc);
    if (result != null) {
      result.detach();
    }
  }

  public void afterPropertiesSet() throws Exception {
    cacheManager = CacheManager.create();
    jobStatusCache = new Cache("jobStatus", 100, false, false, 300, 300);
    cacheManager.addCache(jobStatusCache);
    getUndoManager().addCommandListener(this);
  }

  public void commandStarted(Command command) {
    System.out.println("ZZZ started command id: " + command.getId());
    jobStatusCache.put(new net.sf.ehcache.Element(command.getId(), new CommandResult(JobStatus.STARTED, null)));
  }

  public void commandFinished(Command command) {
    CommandResult result;
    System.out.println("ZZZ finished command id: " + command.getId());
    if (command.getException() != null) {
      logger.error("Command (" + command.getId() + ") failed", command.getException());
      Element error = DocumentHelper.createElement("error");
      error.addElement("message").setText(command.getException().getMessage());
      result = new CommandResult(JobStatus.ERROR, error);
    } else {
      result = new CommandResult(JobStatus.COMPLETED, command.getResult());
    }
    jobStatusCache.put(new net.sf.ehcache.Element(command.getId(), result));
  }

  private static class CommandResult implements Serializable {
    public JobStatus jobStatus;
    public Element result;

    private CommandResult(JobStatus jobStatus, Element result) {
      this.jobStatus = jobStatus;
      this.result = result;
    }
  }
}