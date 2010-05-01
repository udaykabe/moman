package net.deuce.moman.controller;

import net.deuce.moman.job.AbstractCommand;
import net.deuce.moman.job.Command;
import net.deuce.moman.job.Result;
import net.deuce.moman.job.UndoManager;
import net.deuce.moman.om.*;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.quartz.TriggerListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import javax.persistence.NoResultException;
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
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SuppressWarnings("unchecked")
public abstract class AbstractController implements Controller, ApplicationContextAware {

  private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

  private static final String NULL = "null";

  private Logger log = LoggerFactory.getLogger(getClass());

  private Map<Class<AbstractEntity>, Map<String, Method>> methodMap =
      new HashMap<Class<AbstractEntity>, Map<String, Method>>();

  private Pattern commandPattern = Pattern.compile("^[ \t]*([a-zA-Z_0-9]+)[ \t]*\\([ \t]*([a-zA-Z_0-9-]+)?([ \t]*,[ \t]*([a-zA-Z_0-9-]+))*[ \t]*\\)[ \t]*$");

  private ApplicationContext applicationContext;


  @Autowired
  private UserService userService;

  @Autowired
  private UndoManager undoManager;

  public UserService getUserService() {
    return userService;
  }

  protected abstract EntityService getService();

  public void setApplicationContext(ApplicationContext applicationContext) {
    this.applicationContext = applicationContext;
  }

  protected boolean checkParameters(HttpServletRequest req, HttpServletResponse res,
                                    Collection<Parameter> parameters) throws IOException {
    for (Parameter p : parameters) {
      String param = req.getParameter(p.getName());
      if (param == null || param.trim().length() == 0) {
        errorResponse(res, HttpServletResponse.SC_BAD_REQUEST, String.format("parameter '%1$s' is missing", p.getName()));
        return false;
      }
      if (p.getType().equals(Integer.class)) {
        try {
          p.setIntValue(Integer.valueOf(param));
        } catch (NumberFormatException nfe) {
          errorResponse(res, HttpServletResponse.SC_BAD_REQUEST, String.format("parameter '%1$s' is not an number: '%2$s'", p.getName(), param));
          return false;
        }
      } else {
        p.setValue(param);
      }
    }

    return true;
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

  protected Element buildMessageElement(String msg) {
    Element el = DocumentHelper.createElement("message");
    el.setText(msg);
    return el;
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

  public ModelAndView handleRequest(HttpServletRequest req, HttpServletResponse res) throws Exception {

    if (handleDefaultActions(req, res, getService())) return null;

    handleActions(req, res);

    return null;
  }

  protected void handleActions(HttpServletRequest req, HttpServletResponse res) throws IOException {

  }

  protected boolean handleDefaultActions(HttpServletRequest req,
                                         HttpServletResponse res,
                                         EntityService service) throws IOException {

    Parameter uuid;
    Parameter action = new Parameter("action", Integer.class);
    List<Parameter> params = new LinkedList<Parameter>();
    params.add(action);

    try {
      if (!checkParameters(req, res, params)) return true;

      switch (action.getIntValue()) {
        case 0: // NEW
          sendResult(newEntity(service, req, res), res);
          return true;
        case 1: // EDIT
          uuid = new Parameter("uuid", String.class);
          params.add(uuid);
          if (!checkParameters(req, res, params)) return true;

          sendResult(editEntity(uuid.getValue(), service, req, res), res);
          return true;
        case 2: // GET
          uuid = new Parameter("uuid", String.class);
          params.add(uuid);
          if (!checkParameters(req, res, params)) return true;

          getEntity(uuid.getValue(), service, req, res);
          return true;
        case 3: // LIST
          listEntities(service, res);
          return true;
        case 4: // DELETE
          uuid = new Parameter("uuid", String.class);
          params.add(uuid);
          if (!checkParameters(req, res, params)) return true;

          deleteEntity(uuid.getValue(), service, res);
          return true;
        case 5: // LIST PROPERTIES
          listEntityProperties(service, res);
          return true;
        case 6: // LIST COMMANDS
          listServiceCommands(service, res);
          return true;
        case 7: // EXECUTE COMMAND
          executeCommand(service, req, res);
          return true;
      }

      return false;
    } catch (NoResultException e) {
      errorResponse(res, HttpServletResponse.SC_NOT_FOUND, String.format("entity not found"));
      return true;
    } catch (Exception e) {
      String msg = String.format("exception in request: %s", e.getMessage());
      errorResponse(res, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, msg);
      log.error(msg, e);
      return true;
    }
  }

  private void listServiceCommands(EntityService service, HttpServletResponse res) throws IOException {
    Document doc = buildResponse();
    Element root = doc.getRootElement().addElement("commands").addAttribute("type", service.getClass().getName());

    for (Method m : service.getClass().getDeclaredMethods()) {
      if (m.getName().endsWith("Command")) {
        String name = m.getName().substring(0, 1).toLowerCase() + m.getName().substring(1);
        Element command = root.addElement("command").addAttribute("name", name);
        if (m.getReturnType() != null) {
          command.addAttribute("returns", m.getReturnType().getName());
        }
        for (Class type : m.getParameterTypes()) {
          command.addElement("param").addAttribute("type", type.getName());
        }
      }
    }

    sendResponse(res, doc);
  }

  private void executeCommand(EntityService service, HttpServletRequest req, HttpServletResponse res) throws IOException {
    Parameter command = new Parameter("command", String.class);

    if (!checkParameters(req, res, Arrays.asList(new Parameter[]{command}))) return;

    Matcher m = commandPattern.matcher(command.getValue());
    try {
      if (!m.find()) {
        errorResponse(res, HttpServletResponse.SC_BAD_REQUEST, String.format("invalid command call: '%1$s'", command.getValue()));
        return;
      }
    } catch (java.lang.IllegalStateException e) {
      errorResponse(res, HttpServletResponse.SC_BAD_REQUEST, String.format("invalid command call: '%1$s', reason: %2$s", command.getValue(), e.getMessage()));
      return;
    }

//  ([a-zA-Z_0-9]+)[ \t]*\\([ \t]*([a-zA-Z_0-9]+)?([ \t]*,[ \t]*([a-zA-Z_0-9]+))*[ \t]*\\)
    String commandName = m.group(1);
    List<String> argStrings = new LinkedList<String>();
    if (m.groupCount() > 1) {
      if (m.group(2) != null) {
        argStrings.add(m.group(2));
      }
      for (int i = 4; i <= m.groupCount(); i++) {
        if (m.group(i) != null) {
          argStrings.add(m.group(i));
        }
      }
    }

    Method method = getMethod(service, commandName);
    if (method == null) {
      errorResponse(res, HttpServletResponse.SC_NOT_FOUND, String.format("No command named '%1$s'", commandName));
      return;
    }

    if (argStrings.size() != method.getParameterTypes().length) {
      errorResponse(res, HttpServletResponse.SC_BAD_REQUEST, String.format("Mismatched parameters for command '%1$s'", command.getValue()));
      return;
    }

    List<Object> args = new LinkedList<Object>();
    Class[] paramTypes = method.getParameterTypes();

    for (int i = 0; i < paramTypes.length; i++) {

      Class type = paramTypes[i];
      String value = argStrings.get(i);

      if (type.equals(Date.class) || type.equals(Timestamp.class) || type.equals(java.util.Date.class)) {
        try {
          Date date = DATE_FORMAT.parse(value);
          args.add(date);
        } catch (ParseException e) {
          errorResponse(res, HttpServletResponse.SC_BAD_REQUEST, String.format("invalid date format '%1$s', needs to be (yyyy-MM-dd)", value));
          return;
        }
      } else if (type.equals(String.class)) {
        args.add(value);
      } else if (paramTypes[i].getSuperclass().equals(AbstractEntity.class)) {
        EntityService entityService = null;

        if (!paramTypes[i].equals(InternalTransaction.class)) {
          entityService = (EntityService) applicationContext.getBean(type.getSimpleName().substring(0,1).toLowerCase() + type.getSimpleName().substring(1) + "Service", EntityService.class);
        } else {
          entityService = (EntityService) applicationContext.getBean("transactionService", EntityService.class);
        }
        if (entityService == null) {
          errorResponse(res, HttpServletResponse.SC_BAD_REQUEST, String.format("no service found for type '%1$s'", type.getName()));
          return;
        }
        AbstractEntity entity = entityService.getByUuid(value);
        if (entity == null) {
          errorResponse(res, HttpServletResponse.SC_NOT_FOUND, String.format("no " + type.getName() + " entity exist with uuid '%1$s'", value));
          return;
        }
        args.add(entity);
      } else {
        try {
          Method valueOfMethod = type.getDeclaredMethod("valueOf", String.class);
          Object valueOf = valueOfMethod.invoke(null, value);
          args.add(valueOf);
        } catch (NoSuchMethodException e) {
          errorResponse(res, HttpServletResponse.SC_BAD_REQUEST, String.format(type.getName() + " has no valueOf property"));
          return;
        } catch (Exception e) {
          errorResponse(res, HttpServletResponse.SC_BAD_REQUEST, String.format("failed to construct arg list for command '%1$s', reason: %2$s", command.getValue(), e.getMessage()));
          return;
        }
      }
    }

    try {
      Object returnValue = method.invoke(service, args.toArray());
      if (returnValue != null) {
        String value;
        if (returnValue instanceof Date) {
          value = DATE_FORMAT.format(returnValue);
        } else if (returnValue instanceof AbstractEntity) {
          getEntity(((AbstractEntity) returnValue).getUuid(), service, req, res);
          return;
        } else {
          value = returnValue.toString();
        }

        Document doc = buildResponse();
        doc.getRootElement().addElement("return-value").addAttribute("value", value);
        sendResponse(res, doc);
      }
    } catch (Exception e) {
      errorResponse(res, HttpServletResponse.SC_BAD_REQUEST, String.format("failed to execute command '%1$s', reason: %2$s", command.getValue(), e.getMessage()));
    }

  }

  protected void listEntityProperties(EntityService service, HttpServletResponse res) throws IOException {

    Set<String> proposed = new HashSet<String>();
    Set<String> properties = new HashSet<String>();

    for (Method m : service.getType().getDeclaredMethods()) {
      if (m.getName().startsWith("get")) {
        String name = m.getName().substring(3, 4).toLowerCase() + m.getName().substring(4);
        if (proposed.contains("s" + name)) {
          properties.add(name);
        } else {
          proposed.add("g" + name);
        }
      } else if (m.getName().startsWith("set")) {
        String name = m.getName().substring(3, 4).toLowerCase() + m.getName().substring(4);
        if (proposed.contains("g" + name)) {
          properties.add(name);
        } else {
          proposed.add("s" + name);
        }
      }
    }

    Document doc = buildResponse();
    Element root = doc.getRootElement().addElement("properties");

    for (String s : properties) {
      root.addElement("property").addAttribute("name", s);
    }

    sendResponse(res, doc);
  }

  protected Result deleteEntity(String uuid, final EntityService service, final HttpServletResponse res) throws Exception {
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
            setResult(buildEntitiesElement(newEntity, service));
          }
        });
      }
    };

    return undoManager.execute(userService.getStaticUser(), command, null);
  }

  protected void listEntities(EntityService service, HttpServletResponse res) throws IOException {
    Document doc = buildResponse();
    service.toXml(userService.getStaticUser(), doc);
    sendResponse(res, doc);
  }

  protected void getEntity(String uuid, EntityService service, HttpServletRequest req, HttpServletResponse res) throws IOException {
    AbstractEntity entity = service.getByUuid(uuid);
    if (entity == null) {
      errorResponse(res, HttpServletResponse.SC_NOT_FOUND, String.format(service.getType().getSimpleName() + " with uuid '%1$s' does not exist", uuid));
      return;
    }

    sendEntity(entity, service, res);
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

  protected Result editEntity(final String uuid, final EntityService service,
                              final HttpServletRequest req, final HttpServletResponse res) throws Exception {

    AbstractEntity entity = service.getByUuid(uuid);
    if (entity == null) {
      errorResponse(res, HttpServletResponse.SC_NOT_FOUND, String.format(service.getType().getSimpleName() + " with uuid '%1$s' does not exist", uuid));
      return null;
    }

    final Long entityId = entity.getId();

    final List<Parameter> propertyList = new LinkedList<Parameter>();
    String[] properties = req.getParameterValues("property");
    for (String s : properties) {
      String[] split = s.split("=");
      if (split.length < 1) {
        errorResponse(res, HttpServletResponse.SC_BAD_REQUEST, String.format("invalid property param '%1$s'", s));
        return null;
      }
      String name = split[0];
      String value = null;
      if (split.length > 1) {
        value = split[1];
      }
      propertyList.add(new Parameter(name, value));
    }

    final List<Parameter> oldPropertyValues = new LinkedList<Parameter>();
    for (Parameter p : propertyList) {
      String oldValue = getProperty(service, entity, p.getName(), res);
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

        for (Parameter p : propertyList) {
          if (!setProperty(service, entity, p.getName(), p.getValue(), res)) {
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
              if (!setProperty(service, entity, p.getName(), p.getValue(), res)) {
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

    return undoManager.execute(userService.getStaticUser(), command, null);
  }

  private Method getMethod(Object source, String name) {
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

  private Method getGetterMethodForPropertyName(Object source, String name) {
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

  private String getProperty(EntityService service, AbstractEntity entity, String name, HttpServletResponse res) throws IOException {

    Method method = getGetterMethodForPropertyName(entity, name);
    if (method == null) {
      errorResponse(res, HttpServletResponse.SC_BAD_REQUEST, String.format(service.getType().getSimpleName() + " has no property '%1$s'", name));
      return null;
    }

    Class type = method.getReturnType();
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
      return DATE_FORMAT.format((Date) result);
    } else {
      return result.toString();
    }
  }

  private boolean setProperty(EntityService service, AbstractEntity entity, String name, String value,
                              HttpServletResponse res) throws IOException {

    Method method = getSetterMethodForPropertyName(entity, name);
    if (method == null) {
      errorResponse(res, HttpServletResponse.SC_BAD_REQUEST, String.format(service.getType().getSimpleName() + " has no property '%1$s'", name));
      return false;
    }

    Class type = method.getParameterTypes()[0];
    if (type.equals(Date.class) || type.equals(Timestamp.class) || type.equals(java.util.Date.class)) {
      try {
        Date date = DATE_FORMAT.parse(value);
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

  protected Result newEntity(final EntityService service, final HttpServletRequest req, final HttpServletResponse res) throws Exception {

    final List<Parameter> propertyList = new LinkedList<Parameter>();
    String[] properties = req.getParameterValues("property");
    for (String s : properties) {
      String[] split = s.split("=");
      if (split.length < 1) {
        errorResponse(res, HttpServletResponse.SC_BAD_REQUEST, String.format("invalid property param '%1$s'", s));
        return null;
      }
      String name = split[0];
      String value = null;
      if (split.length > 1) {
        value = split[1];
      }
      propertyList.add(new Parameter(name, value));
    }

    Command command = new AbstractCommand("New " + service.getEntityClass().getSimpleName(), true) {

      public void doExecute() throws Exception {
        AbstractEntity entity = service.newEntity();
        for (Parameter p : propertyList) {
          if (!setProperty(service, entity, p.getName(), p.getValue(), res)) {
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

    return undoManager.execute(userService.getStaticUser(), command, null);
  }

  protected UndoManager getUndoManager() {
    return undoManager;
  }

  private Map<String, Method> getMethodMap(Class type) {
    Map<String, Method> m = methodMap.get(type);
    if (m == null) {
      m = new HashMap<String, Method>();
      methodMap.put(type, m);
    }
    return m;
  }

  protected class Parameter {
    private String name;
    private Class type;
    private String value;
    private int intValue;

    public Parameter(String name, Class type) {
      this.name = name;
      this.type = type;
    }

    public Parameter(String name, String value) {
      this.name = name;
      this.value = value;
    }

    public String getName() {
      return name;
    }

    public void setName(String name) {
      this.name = name;
    }

    public Class getType() {
      return type;
    }

    public void setType(Class type) {
      this.type = type;
    }

    public String getValue() {
      return value;
    }

    public void setValue(String value) {
      this.value = value;
    }

    public int getIntValue() {
      return intValue;
    }

    public void setIntValue(int intValue) {
      this.intValue = intValue;
    }

  }

}