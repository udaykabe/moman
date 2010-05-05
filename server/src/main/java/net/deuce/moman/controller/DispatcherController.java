package net.deuce.moman.controller;

import net.deuce.moman.om.EntityService;
import net.deuce.moman.om.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerAdapter;
import org.springframework.web.servlet.HandlerExecutionChain;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;
import org.springframework.web.util.UrlPathHelper;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.LinkedList;
import java.util.List;

public abstract class DispatcherController implements Controller {

  public static final String HANDLER_EXECUTION_CHAIN_ATTRIBUTE = DispatcherController.class.getName() + ".HANDLER";

  public static final String ENTITY_SERVICE_ATTRIBUTE = DispatcherController.class.getName() + ".ENTITY_SERVICE";

  private Logger log = LoggerFactory.getLogger(getClass());

  private List<HandlerMapping> handlerMappings = new LinkedList<HandlerMapping>();
  private List<HandlerAdapter> handlerAdapters = new LinkedList<HandlerAdapter>();

  @Autowired
  private UserService userService;

  protected abstract EntityService getService();

  public ModelAndView handleRequest(HttpServletRequest req, HttpServletResponse res) throws Exception {

    req.setAttribute(ENTITY_SERVICE_ATTRIBUTE, getService());

    HandlerExecutionChain mappedHandler = getHandler(req);
    if (mappedHandler == null || mappedHandler.getHandler() == null) {
      noHandlerFound(req, res);
      return null;
    }

    HandlerAdapter ha = getHandlerAdapter(mappedHandler.getHandler());
    ha.handle(req, res, mappedHandler.getHandler());

    return null;
  }

  protected HandlerAdapter getHandlerAdapter(Object handler) throws ServletException {
    for (HandlerAdapter ha : handlerAdapters) {
      if (log.isTraceEnabled()) {
        log.trace("Testing handler adapter [" + ha + "]");
      }
      if (ha.supports(handler)) {
        return ha;
      }
    }
    throw new ServletException("No adapter for handler [" + handler +
        "]: Does your handler implement a supported interface like Controller?");
  }

  protected void noHandlerFound(HttpServletRequest request, HttpServletResponse response) throws Exception {
    if (log.isWarnEnabled()) {
      String requestUri = new UrlPathHelper().getRequestUri(request);
      log.warn("No mapping found for HTTP request with URI [" + requestUri + "] in DispatchController");
    }
    response.sendError(HttpServletResponse.SC_NOT_FOUND);
  }

  protected HandlerExecutionChain getHandler(HttpServletRequest request) throws Exception {
    HandlerExecutionChain handler =
        (HandlerExecutionChain) request.getAttribute(HANDLER_EXECUTION_CHAIN_ATTRIBUTE);
    if (handler != null) {
      request.removeAttribute(HANDLER_EXECUTION_CHAIN_ATTRIBUTE);
      return handler;
    }

    for (HandlerMapping hm : handlerMappings) {
      handler = hm.getHandler(request);
      if (handler != null) {
        request.setAttribute(HANDLER_EXECUTION_CHAIN_ATTRIBUTE, handler);
        return handler;
      }
    }
    return null;
  }

  public UserService getUserService() {
    return userService;
  }

  public List<HandlerMapping> getHandlerMappings() {
    return handlerMappings;
  }

  public void setHandlerMappings(List<HandlerMapping> handlerMappings) {
    this.handlerMappings = handlerMappings;
  }

  public List<HandlerAdapter> getHandlerAdapters() {
    return handlerAdapters;
  }

  public void setHandlerAdapters(List<HandlerAdapter> handlerAdapters) {
    this.handlerAdapters = handlerAdapters;
  }

}