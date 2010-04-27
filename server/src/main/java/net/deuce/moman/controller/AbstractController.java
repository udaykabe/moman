package net.deuce.moman.controller;

import net.deuce.moman.om.AbstractEntity;
import net.deuce.moman.om.EntityService;
import net.deuce.moman.om.InternalTransaction;
import net.deuce.moman.om.UserService;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
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

  private Map<Class<AbstractEntity>, Map<String, Method>> methodMap =
	  new HashMap<Class<AbstractEntity>, Map<String, Method>>();

  private Pattern functionPattern = Pattern.compile("^[ \t]*([a-zA-Z_0-9]+)[ \t]*\\([ \t]*([a-zA-Z_0-9-]+)?([ \t]*,[ \t]*([a-zA-Z_0-9-]+))*[ \t]*\\)[ \t]*$");

  private ApplicationContext applicationContext;

  @Autowired
  private UserService userService;

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

	protected void errorResponse(HttpServletResponse res, int status, String errorMessage) throws IOException {
	  Document doc = buildResponse();
    Element error = doc.getRootElement().addElement("error");
    error.addElement("message").setText(errorMessage);
	  res.setStatus(status);
	  sendResponse(res, doc);
	}

  protected boolean handleDefaultActions(HttpServletRequest req,
	    HttpServletResponse res,
	    EntityService service) throws IOException {

	  Parameter uuid;
		Parameter action = new Parameter("action", Integer.class);
	  List<Parameter> params = new LinkedList<Parameter>();
	  params.add(action);

    if (!checkParameters(req, res, params)) return true;

	  switch (action.getIntValue()) {
	    case 0: // NEW
	      newEntity(service, req, res);
	      return true;
	    case 1: // EDIT
				uuid = new Parameter("uuid", String.class);
				params.add(uuid);
		    if (!checkParameters(req, res, params)) return true;

	      editEntity(uuid.getValue(), service, req, res);
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

	      deleteEntity(uuid.getValue(), service, req, res);
	      return true;
	    case 5: // LIST PROPERTIES
	      listEntityProperties(service, res);
	      return true;
	    case 6: // LIST FUNCTIONS
	      listServiceMethods(service, res);
	      return true;
	    case 7: // EXECUTE FUNCTIONS
	      executeMethod(service, req, res);
	      return true;
	  }

	  return false;
	}

  private void listServiceMethods(EntityService service, HttpServletResponse res) throws IOException {
	  Document doc = buildResponse();
    Element root = doc.getRootElement().addElement("functions").addAttribute("type", service.getClass().getName());

	  for (Method m : service.getClass().getDeclaredMethods()) {
	    if (!m.getName().startsWith("get") && !m.getName().startsWith("set") && !m.getName().startsWith("is")) {
	      String name = m.getName().substring(0,1).toLowerCase() + m.getName().substring(1);
		    Element function = root.addElement("function").addAttribute("name", name);
		    if (m.getReturnType() != null) {
			    function.addAttribute("returns", m.getReturnType().getName());
		    }
		    for (Class type : m.getParameterTypes()) {
		      function.addElement("param").addAttribute("type", type.getName());
		    }
	    }
	  }

    sendResponse(res, doc);
  }

  private void executeMethod(EntityService service, HttpServletRequest req, HttpServletResponse res) throws IOException {
		Parameter function = new Parameter("function", String.class);

    if (!checkParameters(req, res, Arrays.asList(new Parameter[]{function}))) return;

    Matcher m = functionPattern.matcher(function.getValue());
    try {
	    if (!m.find()) {
	      errorResponse(res, HttpServletResponse.SC_BAD_REQUEST, String.format("invalid function call: '%1$s'", function.getValue()));
	      return;
	    }
    } catch (java.lang.IllegalStateException e) {
	      errorResponse(res, HttpServletResponse.SC_BAD_REQUEST, String.format("invalid function call: '%1$s', reason: %2$s", function.getValue(), e.getMessage()));
	      return;
    }

//  ([a-zA-Z_0-9]+)[ \t]*\\([ \t]*([a-zA-Z_0-9]+)?([ \t]*,[ \t]*([a-zA-Z_0-9]+))*[ \t]*\\)
    String functionName = m.group(1);
    List<String> argStrings = new LinkedList<String>();
    if (m.groupCount() > 1) {
      if (m.group(2) != null) {
	      argStrings.add(m.group(2));
      }
      for (int i=4; i<=m.groupCount(); i++) {
        if (m.group(i) != null) {
	        argStrings.add(m.group(i));
        }
      }
    }

    Method method = getMethod(service, functionName);
    if (method == null) {
        errorResponse(res, HttpServletResponse.SC_NOT_FOUND, String.format("No function named '%1$s'", functionName));
        return;
    }

    if (argStrings.size() != method.getParameterTypes().length) {
        errorResponse(res, HttpServletResponse.SC_BAD_REQUEST, String.format("Mismatched parameters for function '%1$s'", function.getValue()));
        return;
    }

    List<Object> args = new LinkedList<Object>();
    Class[] paramTypes = method.getParameterTypes();

    for (int i=0; i<paramTypes.length; i++) {

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

        if (paramTypes[i].equals(InternalTransaction.class)) {
          entityService = (EntityService) applicationContext.getBean(type.getSimpleName() + "Service", EntityService.class);
        } else {
          entityService = (EntityService) applicationContext.getBean("TransactionService", EntityService.class);
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
          errorResponse(res, HttpServletResponse.SC_BAD_REQUEST, String.format("failed to construct arg list for function '%1$s', reason: %2$s", function.getValue(), e.getMessage()));
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
          getEntity(((AbstractEntity)returnValue).getUuid(), service, req, res);
          return;
        } else {
          value = returnValue.toString();
        }

        Document doc = buildResponse();
        doc.getRootElement().addElement("return-value").addAttribute("value", value);
        sendResponse(res, doc);
      }
    } catch (Exception e) {
          errorResponse(res, HttpServletResponse.SC_BAD_REQUEST, String.format("failed to execute function '%1$s', reason: %2$s", function.getValue(), e.getMessage()));
    }

  }

  protected void listEntityProperties(EntityService service, HttpServletResponse res) throws IOException {

	  Set<String> proposed = new HashSet<String>();
	  Set<String> properties = new HashSet<String>();

	  for (Method m : service.getType().getDeclaredMethods()) {
	    if (m.getName().startsWith("get")) {
	      String name = m.getName().substring(3,4).toLowerCase() + m.getName().substring(4);
	      if (proposed.contains("s"+name)) {
	        properties.add(name);
	      } else {
	        proposed.add("g"+name);
	      }
	    } else if (m.getName().startsWith("set")) {
	      String name = m.getName().substring(3,4).toLowerCase() + m.getName().substring(4);
	      if (proposed.contains("g"+name)) {
	        properties.add(name);
	      } else {
	        proposed.add("s"+name);
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

  protected void deleteEntity(String uuid, EntityService service, HttpServletRequest req, HttpServletResponse res) throws IOException {
	  AbstractEntity entity = service.getByUuid(uuid);
    if (entity == null) {
      errorResponse(res, HttpServletResponse.SC_NOT_FOUND, String.format(service.getType().getSimpleName() + " with uuid '%1$s' does not exist", uuid));
      return;
    }
    service.delete(entity);
    res.setStatus(HttpServletResponse.SC_OK);
  }

  protected void listEntities(EntityService service, HttpServletResponse res) throws IOException {
    Document doc = buildResponse();
    service.toXml(userService.findByUsername("nbolton"), doc);
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

  protected void sendEntity(AbstractEntity entity, EntityService service, HttpServletResponse res) throws IOException {

    Document doc = buildResponse();
    Element root = doc.getRootElement().addElement(service.getRootElementName());
    service.toXml(entity, root);
    sendResponse(res, doc);
  }

  protected void editEntity(String uuid, EntityService service,
                            HttpServletRequest req, HttpServletResponse res) throws IOException {

    AbstractEntity entity = service.getByUuid(uuid);
    if (entity == null) {
      errorResponse(res, HttpServletResponse.SC_NOT_FOUND, String.format(service.getType().getSimpleName() + " with uuid '%1$s' does not exist", uuid));
      return;
    }

    String[] properties = req.getParameterValues("property");
    for (String s : properties) {
      String[] split = s.split("=");
      if (split.length < 1) {
	      errorResponse(res, HttpServletResponse.SC_BAD_REQUEST, String.format("invalid property param '%1$s'", s));
        return;
      }
      String name = split[0];
      String value = null;
      if (split.length > 1) {
        value = split[1];
      }
      if (!setProperty(service, entity, name, value, res)) {
        return;
      }
    }
    getEntity(uuid, service, req, res);
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
    Method method = map.get(name);
    if (method == null) {
		  for (Method m : source.getClass().getDeclaredMethods()) {
		    if (m.getName().startsWith("set")) {
		      String setterName = m.getName().substring(3,4).toLowerCase() + m.getName().substring(4);
		      if (setterName.equals(name)) {
		        method = m;
		        map.put(name, m);
		        break;
		      }
		    }
		  }
    }
    return method;
  }

  private boolean setProperty(EntityService service, AbstractEntity entity, String name, String value,
                              HttpServletResponse res) throws IOException {

    Method method = getSetterMethodForPropertyName(service, name);
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

  protected void newEntity(EntityService service, HttpServletRequest req, HttpServletResponse res) throws IOException {

    AbstractEntity entity = service.newEntity();
    sendEntity(entity, service, res);
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
      super();
      this.name = name;
      this.type = type;
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