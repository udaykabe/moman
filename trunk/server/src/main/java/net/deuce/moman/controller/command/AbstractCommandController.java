package net.deuce.moman.controller.command;

import net.deuce.moman.controller.DispatcherController;
import net.deuce.moman.job.Result;
import net.deuce.moman.job.UndoManager;
import net.deuce.moman.om.AbstractEntity;
import net.deuce.moman.om.EntityService;
import net.deuce.moman.om.UserService;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.web.servlet.mvc.Controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Method;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public abstract class AbstractCommandController implements Controller, ApplicationContextAware {

  protected static final String NULL = "null";

  @Autowired
  private UndoManager undoManager;

  @Autowired
  private UserService userService;

  private ApplicationContext applicationContext;
  private Map<Class<AbstractEntity>, Map<String, Method>> methodMap = new HashMap<Class<AbstractEntity>, Map<String, Method>>();
  private DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

  public EntityService getService(HttpServletRequest req) {
    return (EntityService) req.getAttribute(DispatcherController.ENTITY_SERVICE_ATTRIBUTE);
  }

  public UserService getUserService() {
    return userService;
  }

  public void setUserService(UserService userService) {
    this.userService = userService;
  }

  public DateFormat getDateFormat() {
    return dateFormat;
  }

  public void setDateFormat(DateFormat dateFormat) {
    this.dateFormat = dateFormat;
  }

  public ApplicationContext getApplicationContext() {
    return applicationContext;
  }

  public void setApplicationContext(ApplicationContext applicationContext) {
    this.applicationContext = applicationContext;
  }

  public UndoManager getUndoManager() {
    return undoManager;
  }

  public void setUndoManager(UndoManager undoManager) {
    this.undoManager = undoManager;
  }

  protected String getUuidParameter(HttpServletRequest req, HttpServletResponse res) throws IOException {
    String[] pathInfo = req.getPathInfo().split("/");
    if (pathInfo.length < 4) {
      errorResponse(res, HttpServletResponse.SC_BAD_REQUEST, "No uuid parameter given");
      return null;
    }

    return pathInfo[3];
  }

  protected Document buildResponse() {
    Document doc = DocumentHelper.createDocument();
    doc.addElement("moman");
    return doc;
  }

  protected void sendResponse(HttpServletResponse res, Document doc) throws IOException {
    OutputFormat format = OutputFormat.createPrettyPrint();
    XMLWriter writer = new XMLWriter(res.getWriter(), format);
    writer.write(doc);
  }

  protected void errorResponse(HttpServletResponse res, int status, Exception exception) throws IOException {
    Document doc = buildResponse();
    Element error = doc.getRootElement().addElement("error");
    error.addElement("message").setText(exception.getMessage());
    StringWriter sw = new StringWriter();
    exception.printStackTrace(new PrintWriter(sw));
    error.addElement("trace").setText(sw.toString());
    res.setStatus(status);
    sendResponse(res, doc);
  }

  protected void errorResponse(HttpServletResponse res, int status, String errorMessage) throws IOException {
    Document doc = buildResponse();
    Element error = doc.getRootElement().addElement("error");
    error.addElement("message").setText(errorMessage);
    res.setStatus(status);
    sendResponse(res, doc);
  }

  protected void sendResult(Result result, HttpServletResponse res) throws IOException {
    if (result != null && result.getResult() != null) {
      Document doc = buildResponse();
      doc.getRootElement().add(result.getResult());
      sendResponse(res, doc);
    }
  }

  protected Element buildEntitiesElement(AbstractEntity entity, EntityService service) {
    Element el = DocumentHelper.createElement(service.getRootElementName());
    service.toXml(entity, el);
    return el;
  }

  protected void sendEntity(AbstractEntity entity, EntityService service, HttpServletResponse res) throws IOException {
    Document doc = buildResponse();
    Element root = doc.getRootElement().addElement(service.getRootElementName());
    service.toXml(entity, root);
    sendResponse(res, doc);
  }

  protected String getProperty(EntityService service, AbstractEntity entity, String name, HttpServletResponse res) throws IOException {

    Method method = getGetterMethodForPropertyName(entity, name);
    if (method == null) {
      errorResponse(res, HttpServletResponse.SC_BAD_REQUEST, String.format(service.getType().getSimpleName() + " has no property '%1$s'", name));
      return null;
    }

    Object result = null;
    try {
      result = method.invoke(entity);
    } catch (Exception e) {
      errorResponse(res, HttpServletResponse.SC_BAD_REQUEST, String.format("failed to get '%1$s' property: %2$s", name, e.getMessage()));
      return null;
    }

    if (result == null) {
      return NULL;
    } else if (result instanceof Date) {
      return dateFormat.format((Date) result);
    } else {
      return result.toString();
    }
  }

  protected boolean setProperty(EntityService service, AbstractEntity entity, String name, String value,
                              HttpServletResponse res) throws IOException {

    Method method = getSetterMethodForPropertyName(entity, name);
    if (method == null) {
      errorResponse(res, HttpServletResponse.SC_BAD_REQUEST, String.format(service.getType().getSimpleName() + " has no property '%1$s'", name));
      return false;
    }

    Class type = method.getParameterTypes()[0];
    if (type.equals(Date.class) || type.equals(Timestamp.class) || type.equals(java.util.Date.class)) {
      try {
        Date date = dateFormat.parse(value);
        method.invoke(entity, date);
      } catch (ParseException e) {
        errorResponse(res, HttpServletResponse.SC_BAD_REQUEST, String.format("invalid date format '%1$s', needs to be (yyyy-MM-dd)", value));
        return false;
      } catch (Exception e) {
        errorResponse(res, HttpServletResponse.SC_BAD_REQUEST, String.format("failed to set Date '%1$s' property with value '%2$s': %3$s", name, value, e.getMessage()));
        return false;
      }
    } else if (type.equals(String.class)) {
      try {
        method.invoke(entity, value);
      } catch (Exception e) {
        errorResponse(res, HttpServletResponse.SC_BAD_REQUEST, String.format("failed to set String '%1$s' property with value '%2$s': %3$s", name, value, e.getMessage()));
        return false;
      }
    } else {
      try {
        Method valueOfMethod = type.getDeclaredMethod("valueOf", String.class);
        Object valueOf = valueOfMethod.invoke(null, value);
        method.invoke(entity, valueOf);
      } catch (NoSuchMethodException e) {
        errorResponse(res, HttpServletResponse.SC_BAD_REQUEST, String.format(type.getName() + " has no valueOf property", name));
        return false;
      } catch (Exception e) {
        errorResponse(res, HttpServletResponse.SC_BAD_REQUEST, String.format("failed to set '%1$s' property with value '%2$s': %3$s", name, value, e.getMessage()));
        return false;
      }
    }

    return true;
  }

  protected Method getGetterMethodForPropertyName(Object source, String name) {
    Map<String, Method> map = getMethodMap(source.getClass());
    String key = "get" + name;
    Method method = map.get(key);
    if (method == null) {
      for (Method m : source.getClass().getDeclaredMethods()) {
        for (String prefix : new String[]{"get", "is"}) {
          if (m.getName().startsWith(prefix)) {
            int len = prefix.length();
            String getterName = m.getName().substring(len, len + 1).toLowerCase() + m.getName().substring(len + 1);
            if (getterName.equals(name)) {
              method = m;
              map.put(key, m);
              break;
            }
          }
        }
      }
    }
    return method;
  }


  protected Method getMethod(Object source, String name) {
    Map<String, Method> map = getMethodMap(source.getClass());
    Method method = map.get(name);
    if (method == null) {
      for (Method m : source.getClass().getDeclaredMethods()) {
        if (m.getName().equals(name)) {
          method = m;
          map.put(name, m);
          break;
        }
      }
    }
    return method;
  }

  private Method getSetterMethodForPropertyName(Object source, String name) {
    Map<String, Method> map = getMethodMap(source.getClass());
    String key = "set" + name;
    Method method = map.get(key);
    if (method == null) {
      for (Method m : source.getClass().getDeclaredMethods()) {
        if (m.getName().startsWith("set")) {
          String setterName = m.getName().substring(3, 4).toLowerCase() + m.getName().substring(4);
          if (setterName.equals(name)) {
            method = m;
            map.put(key, m);
            break;
          }
        }
      }
    }
    return method;
  }

  private Map<String, Method> getMethodMap(Class type) {
    Map<String, Method> m = methodMap.get(type);
    if (m == null) {
      m = new HashMap<String, Method>();
      methodMap.put(type, m);
    }
    return m;
  }
}