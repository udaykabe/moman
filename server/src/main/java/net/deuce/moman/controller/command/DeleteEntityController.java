package net.deuce.moman.controller.command;

import net.deuce.moman.job.AbstractCommand;
import net.deuce.moman.job.Command;
import net.deuce.moman.job.Result;
import net.deuce.moman.om.AbstractEntity;
import net.deuce.moman.om.EntityService;
import org.dom4j.Element;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;

public class DeleteEntityController extends AbstractCommandController {

  public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {

    String uuid = getUuidParameter(request, response);

    if (uuid != null) {
      sendResult(deleteEntity(uuid, request, response), response);
    }

    return null;
  }

  protected Result deleteEntity(String uuid, final HttpServletRequest req, final HttpServletResponse res) throws Exception {
    final EntityService service = getService(req);
    AbstractEntity entity = service.getByUuid(uuid);
    if (entity == null) {
      errorResponse(res, HttpServletResponse.SC_NOT_FOUND, String.format(service.getType().getSimpleName() + " with uuid '%1$s' does not exist", uuid));
      return null;
    }

    final Long entityId = entity.getId();

    Command command = new AbstractCommand("Delete " + service.getEntityClass().getSimpleName() + "(" + uuid + ")", true) {

      public void doExecute() throws Exception {

        final AbstractEntity entity = service.get(entityId);

        service.delete(entity);

        setResultCode(HttpServletResponse.SC_OK);

        setUndo(new AbstractCommand("Undo " + getName(), true) {
          public void doExecute() throws Exception {

            entity.setId(null);
            AbstractEntity newEntity = service.saveOrUpdate(entity);

            setResultCode(HttpServletResponse.SC_OK);
            setResult(Arrays.asList(new Element[]{buildEntitiesElement(newEntity, service)}));
          }
        });
      }
    };

    return getUndoManager().execute(getUserService().getDefaultUser(), command, null);
  }
}
