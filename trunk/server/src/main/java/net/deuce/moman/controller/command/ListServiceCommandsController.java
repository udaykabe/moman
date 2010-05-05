package net.deuce.moman.controller.command;

import net.deuce.moman.om.EntityService;
import org.dom4j.Document;
import org.dom4j.Element;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Method;

public class ListServiceCommandsController extends AbstractCommandController {

  public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
    listServiceCommands(request, response);
    return null;
  }

  protected void listServiceCommands(HttpServletRequest req, HttpServletResponse res) throws IOException {
    final EntityService service = getService(req);
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
}
