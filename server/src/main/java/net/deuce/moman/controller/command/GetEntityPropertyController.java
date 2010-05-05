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
import java.util.Collection;
import java.util.Date;

public class GetEntityPropertyController extends EntityAccessingController {

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

    EntityService service = getService(req);

    Document doc = buildResponse();
    Element entityProperty = doc.getRootElement().addElement("entity-property")
        .addAttribute("type", service.getEntityClass().getSimpleName())
        .addAttribute("uuid", uuid);
    entityProperty.addElement("name").setText(property);
    Element valueElement = entityProperty.addElement("value");

    EntityResult result = getEntityAdapter().getProperty(service, uuid, property);
    if (result.getException() != null || result.getMessage() != null) {
      errorResponse(res, result.getResponseCode(), result.getException(), result.getMessage());
      return;
    }

    EntityService entityService;
    Object value = result.getValue();

    if (value instanceof AbstractEntity) {

      if (value instanceof InternalTransaction) {
        entityService = (EntityService) getApplicationContext().getBean("transactionService", EntityService.class);
      } else {
        String type = value.getClass().getSimpleName().replaceAll("_.._javassist_[0-9]*", "");
        entityService = (EntityService) getApplicationContext().getBean(type.substring(0, 1).toLowerCase() + type.substring(1) + "Service", EntityService.class);
      }

      Element root = valueElement.addElement(entityService.getRootElementName());
      entityService.toXml((AbstractEntity) value, root);
    } else if (value instanceof Collection) {

      Collection<AbstractEntity> entities = (Collection<AbstractEntity>) value;
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
    } else if (value == null) {
      valueElement.addAttribute("null", "true");
    } else if (value instanceof Date) {
      valueElement.setText(getDateFormat().format((Date) value));
    } else {
      valueElement.setText(value.toString());
    }
    sendResponse(res, doc);
  }
}
