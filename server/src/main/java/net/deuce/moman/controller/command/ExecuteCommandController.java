package net.deuce.moman.controller.command;

import net.deuce.moman.job.Command;
import net.deuce.moman.job.Result;
import net.deuce.moman.job.UndoManager;
import org.dom4j.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URLDecoder;
import java.util.LinkedList;
import java.util.List;

public class ExecuteCommandController extends AbstractCommandController {

  @Autowired
  private GetEntityController getEntityController;

  @Autowired
  private DynamicCommandBuilder dynamicCommandBuilder;

  @Autowired
  private UndoManager undoManager;

  public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {

    String[] pathInfo = request.getPathInfo().split("/");

    if (pathInfo.length < 4) {
      errorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "No command given");
      return null;
    }

    List<String> command = new LinkedList<String>();

    for (int i = 3; i < pathInfo.length; i++) {
      command.add(URLDecoder.decode(pathInfo[i], "UTF-8"));
    }

    executeCommand(command, request, response);

    return null;
  }

  protected void executeCommand(List<String> command, HttpServletRequest req, HttpServletResponse res) throws Exception {

    String commandName = command.get(0);
    command.remove(0);

    CommandBuilderResult builderResult = dynamicCommandBuilder.buildCommand(getService(req), commandName, command);
    if (builderResult.getException() != null || builderResult.getMessage() != null) {
      errorResponse(res, builderResult.getResponseCode(), builderResult.getException(), builderResult.getMessage());
      return;
    }

    Command cmd = builderResult.getCommand();
    Result result = undoManager.execute(getUserService().getStaticUser(), cmd, null);

    if (result.getResult() != null) {
      Document doc = buildResponse();
      doc.getRootElement().add(result.getResult());
      sendResponse(res, doc);
    }

  }
}
