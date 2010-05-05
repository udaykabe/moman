package net.deuce.moman.controller.command;

import net.deuce.moman.om.AbstractEntity;
import net.deuce.moman.om.EntityService;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class GetEntityController extends AbstractCommandController {

  public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {

    String[] pathInfo = request.getPathInfo().split("/");
    if (pathInfo.length != 4) {
      errorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "No uuid parameter given");
      return null;
    }

    String uuid = pathInfo[3];

    getEntity(uuid, request, response);

    return null;
  }

  protected void getEntity(String uuid, HttpServletRequest req, HttpServletResponse res) throws IOException {
    final EntityService service = getService(req);
    AbstractEntity entity = service.getByUuid(uuid);
    if (entity == null) {
      errorResponse(res, HttpServletResponse.SC_NOT_FOUND, String.format(service.getType().getSimpleName() + " with uuid '%1$s' does not exist", uuid));
      return;
    }

    sendEntity(entity, service, res);
  }
}
