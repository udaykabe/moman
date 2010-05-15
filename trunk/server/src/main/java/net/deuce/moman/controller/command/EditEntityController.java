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
import java.net.URLDecoder;
import java.util.LinkedList;
import java.util.List;

public class EditEntityController extends EntityAccessingController {


  public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {

    String uuid = getUuidParameter(request, response);
    if (uuid == null) return null;

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
      properties.add(new Parameter(URLDecoder.decode(pathInfo[i], "UTF-8"), URLDecoder.decode(pathInfo[i + 1], "UTF-8")));
    }

    sendResult(editEntity(uuid, properties, request, response), response);

    return null;
  }

  protected Result editEntity(final String uuid, final List<Parameter> properties,
                              final HttpServletRequest req, final HttpServletResponse res) throws Exception {

    final EntityService service = getService(req);
    AbstractEntity entity = service.getByUuid(uuid);
    if (entity == null) {
      errorResponse(res, HttpServletResponse.SC_NOT_FOUND, String.format(service.getType().getSimpleName() + " with uuid '%1$s' does not exist", uuid));
      return null;
    }

    final Long entityId = entity.getId();

    final List<Parameter> oldPropertyValues = new LinkedList<Parameter>();
    for (Parameter p : properties) {

      EntityResult entityResult = getEntityAdapter().getProperty(service, entity, p.getName());
      if (entityResult.getException() != null || entityResult.getMessage() != null) {
        errorResponse(res, entityResult.getResponseCode(), entityResult.getException(), entityResult.getMessage());
        return null;
      }

      String oldValue = (String) entityResult.getValue();
      if (oldValue == null) return null;

      // NULL is used to distinguish between getProperty returning null (which means there was an error)
      // and the property value being null
      if (oldValue == NULL) {
        oldValue = null;
      }
      oldPropertyValues.add(new Parameter(p.getName(), oldValue));
    }

    Command command = new AbstractCommand("Edit " + service.getEntityClass().getSimpleName() + "(" + uuid + ")", true) {

      public void doExecute() throws Exception {

        AbstractEntity entity = service.get(entityId);

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

        setUndo(new AbstractCommand("Undo " + getName(), true) {
          public void doExecute() throws Exception {
            AbstractEntity entity = service.get(entityId);

            for (Parameter p : oldPropertyValues) {
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
          }
        });
      }
    };

    return getUndoManager().execute(getUserService().getDefaultUser(), command, null);
  }

}
