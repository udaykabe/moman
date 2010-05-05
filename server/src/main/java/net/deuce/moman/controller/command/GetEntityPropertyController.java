package net.deuce.moman.controller.command;

import net.deuce.moman.om.AbstractEntity;
import net.deuce.moman.om.EntityService;
import net.deuce.moman.om.InternalTransaction;
import org.dom4j.Document;
import org.dom4j.Element;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Date;

public class GetEntityPropertyController extends AbstractCommandController {

  public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {

    String uuid = getUuidParameter(request, response);
    if (uuid == null) return null;

    String[] pathInfo = request.getPathInfo().split("/");

    if (pathInfo.length < 5) {
      errorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "No property parameter given");
      return null;
    }

    String property = pathInfo[4];

    getEntityProperty(uuid, property, request, response);

    return null;
  }

  protected void getEntityProperty(String uuid, String property, HttpServletRequest req, HttpServletResponse res) throws IOException {

    final EntityService service = getService(req);
    AbstractEntity entity = service.findEntity(uuid);

    if (entity == null) {
      errorResponse(res, HttpServletResponse.SC_NOT_FOUND, String.format("No %1$s exists with uuid = '%2$s'", service.getEntityClass().getSimpleName(), uuid));
      return;
    }

    Method method = getGetterMethodForPropertyName(entity, property);
    if (method == null) {
      errorResponse(res, HttpServletResponse.SC_BAD_REQUEST, String.format(service.getType().getSimpleName() + " has no property '%1$s'", property));
      return;
    }

    Object result = null;
    try {
      result = method.invoke(entity);
    } catch (Exception e) {
      errorResponse(res, HttpServletResponse.SC_BAD_REQUEST, String.format("failed to get '%1$s' property: %2$s", property, e.getMessage()));
      return;
    }

    EntityService entityService;

    Document doc = buildResponse();
    Element entityProperty = doc.getRootElement().addElement("entity-property")
        .addAttribute("type", service.getEntityClass().getSimpleName())
        .addAttribute("uuid", uuid);
    entityProperty.addElement("name").setText(property);
    Element valueElement = entityProperty.addElement("value");

    if (result instanceof AbstractEntity) {

      if (result instanceof InternalTransaction) {
        entityService = (EntityService) getApplicationContext().getBean("transactionService", EntityService.class);
      } else {
        String type = result.getClass().getSimpleName().replaceAll("_.._javassist_[0-9]*", "");
        entityService = (EntityService) getApplicationContext().getBean(type.substring(0, 1).toLowerCase() + type.substring(1) + "Service", EntityService.class);
      }

      Element root = valueElement.addElement(entityService.getRootElementName());
      entityService.toXml((AbstractEntity) result, root);
    } else if (result instanceof Collection) {

      Collection<AbstractEntity> entities = (Collection<AbstractEntity>) result;
      if (entities.size() > 0) {
        AbstractEntity firstEntity = entities.iterator().next();

        if (firstEntity instanceof InternalTransaction) {
          entityService = (EntityService) getApplicationContext().getBean("transactionService", EntityService.class);
        } else {
          String type = firstEntity.getClass().getSimpleName().replaceAll("_.._javassist_[0-9]*", "");
          entityService = (EntityService) getApplicationContext().getBean(type.substring(0, 1).toLowerCase() + type.substring(1) + "Service", EntityService.class);
        }

        Element root = valueElement.addElement(service.getRootElementName());

        for (AbstractEntity ae : entities) {
          entityService.toXml(ae, root);
        }
      }
    } else if (result == null) {
      valueElement.addAttribute("null", "true");
    } else if (result instanceof Date) {
      valueElement.setText(getDateFormat().format((Date) result));
    } else {
      valueElement.setText(result.toString());
    }
    sendResponse(res, doc);
  }
}
