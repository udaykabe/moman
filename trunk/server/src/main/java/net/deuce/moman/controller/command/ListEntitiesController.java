package net.deuce.moman.controller.command;

import net.deuce.moman.om.EntityService;
import net.deuce.moman.om.UserBasedService;
import org.dom4j.Document;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class ListEntitiesController extends AbstractCommandController {

  public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
    listEntities(request, response);
    return null;
  }

  protected void listEntities(HttpServletRequest req, HttpServletResponse res) throws IOException {
    Document doc = buildResponse();

    EntityService service = getService(req);
    if (service instanceof UserBasedService) {
      ((UserBasedService) service).toXml(getUserService().getDefaultUser(), doc);
    } else {
      service.toXml(doc);
    }
    sendResponse(res, doc);
  }
}
