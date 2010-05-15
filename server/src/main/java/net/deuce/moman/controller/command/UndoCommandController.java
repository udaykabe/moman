package net.deuce.moman.controller.command;

import net.deuce.moman.job.Result;
import net.deuce.moman.om.User;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class UndoCommandController extends AbstractJobCommandController {

  public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
    Result result = undo();
    if (result != null && (result.getResult() != null || result.getResultCode() == HttpServletResponse.SC_OK)) {
      sendResult(result, response);
    } else {
      response.setStatus(HttpServletResponse.SC_NOT_FOUND);
    }

    return null;
  }

  protected Result undo() throws Exception {
    User user = getUserService().getDefaultUser();
    return new Result(HttpServletResponse.SC_OK, getUndoManager().undo(user));
  }
}