package net.deuce.moman.controller.command;

import net.deuce.moman.controller.Parameter;
import net.deuce.moman.job.AbstractCommand;
import net.deuce.moman.job.Command;
import net.deuce.moman.job.Result;
import net.deuce.moman.om.AbstractEntity;
import net.deuce.moman.om.EntityService;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.LinkedList;
import java.util.List;

public class NewEntityController extends EntityAccessingController {

  public ModelAndView handleRequest(HttpServletRequest request, final HttpServletResponse response) throws Exception {

    String[] pathInfo = request.getPathInfo().split("/");

    if (pathInfo.length < 6) {
      errorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "No properties/values given");
      return null;
    }

    if (((pathInfo.length - 4) % 2) != 0) {
      errorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "Mismatched property/value list");
      return null;
    }

    List<Parameter> properties = new LinkedList<Parameter>();

    for (int i = 4; i < pathInfo.length; i += 2) {
      properties.add(new Parameter(pathInfo[i], pathInfo[i + 1]));
    }

    sendResult(newEntity(properties, request, response), response);
    return null;
  }

  protected Result newEntity(final List<Parameter> properties, final HttpServletRequest request, final HttpServletResponse response) throws Exception {

    final EntityService service = getService(request);
    Command command = new AbstractCommand("New " + service.getEntityClass().getSimpleName(), true) {

      public void doExecute() throws Exception {
        AbstractEntity entity = service.newEntity();
        for (Parameter p : properties) {
          EntityResult entityResult = getEntityAdapter().setProperty(service, entity, p.getName(), p.getValue());
          if (entityResult.getException() != null || entityResult.getMessage() != null) {
            setResultCode(entityResult.getResponseCode());
            setResult(buildErrorResponse(entityResult.getException(), entityResult.getMessage()));
            return;
          }
        }

        entity = service.saveOrUpdate(entity);

        setResultCode(HttpServletResponse.SC_OK);
        setResult(buildEntitiesElement(entity, service));

        final Long entityId = entity.getId();
        setUndo(new AbstractCommand("Undo " + getName(), true) {
          public void doExecute() throws Exception {
            service.delete(service.get(entityId));
            setResultCode(HttpServletResponse.SC_OK);
          }
        });
      }
    };

    return getUndoManager().execute(getUserService().getStaticUser(), command, null);
  }
}
