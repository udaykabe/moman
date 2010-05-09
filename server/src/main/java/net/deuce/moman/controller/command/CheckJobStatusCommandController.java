package net.deuce.moman.controller.command;

import net.deuce.moman.controller.JobStatus;
import org.dom4j.Document;
import org.dom4j.Element;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLDecoder;

public class CheckJobStatusCommandController extends AbstractJobCommandController {

  public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {

    String[] pathInfo = request.getPathInfo().split("/");
    if (pathInfo.length != 4) {
      errorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "No uuid parameter given");
      return null;
    }

    String uuid = URLDecoder.decode(pathInfo[3], "UTF-8");

    checkJobStatus(uuid, response);

    return null;
  }

  protected void checkJobStatus(String uuid, HttpServletResponse res) throws IOException {
    net.sf.ehcache.Element element = getCache().get(uuid);
    JobStatus status = JobStatus.NONE;
    Element result = null;
    if (element != null) {
      status = ((CommandResult) element.getValue()).getJobStatus();
      result = ((CommandResult) element.getValue()).getResult();
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
}