package net.deuce.moman.controller.command;

import org.dom4j.Document;
import org.dom4j.Element;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

public class ListEntityPropertiesController extends AbstractCommandController {

  public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
    listEntityProperties(request, response);
    return null;
  }

  protected void listEntityProperties(HttpServletRequest req, HttpServletResponse res) throws IOException {

    Set<String> proposed = new HashSet<String>();
    Set<String> properties = new HashSet<String>();

    for (Method m : getService(req).getType().getDeclaredMethods()) {
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
}
