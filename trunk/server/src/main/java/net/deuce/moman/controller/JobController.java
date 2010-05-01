package net.deuce.moman.controller;

import net.deuce.moman.job.CommandWrappedTrigger;
import net.deuce.moman.job.Result;
import net.deuce.moman.om.AllocationSetService;
import net.deuce.moman.om.EntityService;
import net.deuce.moman.om.User;
import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import org.dom4j.Document;
import org.dom4j.Element;
import org.quartz.JobExecutionContext;
import org.quartz.Trigger;
import org.quartz.TriggerListener;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class JobController extends AbstractController implements InitializingBean, TriggerListener {

  private static enum JobStatus {
    NONE, STARTED, COMPLETED
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
      case 8: // JOB STATUS
        uuid = new Parameter("uuid", String.class);
        params.add(uuid);
        if (!checkParameters(req, res, params)) return null;

        checkJobStatus(uuid.getValue(), req, res);
        break;
      case 9: // UNDO
        result = undo();
        if (result != null && result.getResult() != null) {
          sendResult(result, res);
        } else {
          res.setStatus(HttpServletResponse.SC_NOT_FOUND);
        }
        break;
      case 10: // REDO
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
    if (element != null) {
      status = (JobStatus) element.getValue();
    }

    Document doc = buildResponse();
    Element root = doc.getRootElement()
        .addElement("job-status")
        .addAttribute("uuid", uuid)
        .addAttribute("status", status.name());
    sendResponse(res, doc);
  }

  public void afterPropertiesSet() throws Exception {
    cacheManager = CacheManager.create();
    jobStatusCache = new Cache("jobStatus", 100, false, false, 300, 300);
    cacheManager.addCache(jobStatusCache);
    getUndoManager().addTriggerListener(this);
  }

  public String getName() {
    return getClass().getName();
  }

  public void triggerFired(Trigger trigger, JobExecutionContext jobExecutionContext) {
    jobStatusCache.put(new net.sf.ehcache.Element(((CommandWrappedTrigger) trigger).getId(), JobStatus.STARTED));
  }

  public boolean vetoJobExecution(Trigger trigger, JobExecutionContext jobExecutionContext) {
    return false;
  }

  public void triggerMisfired(Trigger trigger) {
  }

  public void triggerComplete(Trigger trigger, JobExecutionContext jobExecutionContext, int i) {
    jobStatusCache.put(new net.sf.ehcache.Element(((CommandWrappedTrigger) trigger).getId(), JobStatus.COMPLETED));
  }
}