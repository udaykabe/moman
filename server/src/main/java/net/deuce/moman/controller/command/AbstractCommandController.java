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
import java.text.DateFormat;
import java.text.SimpleDateFormat;

public abstract class AbstractCommandController implements Controller, ApplicationContextAware {

  protected static final String NULL = "null";

  @Autowired
  private UndoManager undoManager;

  @Autowired
  private UserService userService;

  private ApplicationContext applicationContext;
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

  protected Element buildErrorResponse(Exception exception, String message) {
    Element error = DocumentHelper.createElement("error");

    if (message != null) {
      error.addElement("message").setText(message);
    } else {
      error.addElement("message").setText(exception.getMessage());
    }

    if (exception != null) {
      StringWriter sw = new StringWriter();
      exception.printStackTrace(new PrintWriter(sw));
      error.addElement("trace").setText(sw.toString());
    }
    return error;
  }

  protected void errorResponse(HttpServletResponse res, int status, Exception exception, String message) throws IOException {
    Document doc = buildResponse();
    doc.getRootElement().add(buildErrorResponse(exception, message));
    res.setStatus(status);
    sendResponse(res, doc);
  }

  protected void errorResponse(HttpServletResponse res, int status, String errorMessage) throws IOException {
    Document doc = buildResponse();
    doc.getRootElement().add(buildErrorResponse(null, errorMessage));
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

}