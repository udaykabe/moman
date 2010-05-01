package net.deuce.moman.controller;

import net.deuce.moman.om.EntityService;
import net.deuce.moman.om.EnvelopeService;
import net.deuce.moman.om.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;

public class SavingsGoalController extends AbstractController {

  @Autowired
  private EnvelopeService envelopeService;

  @Autowired
  private UserService userService;

  protected EntityService getService() {
    return envelopeService;
  }

  public ModelAndView handleRequest(HttpServletRequest req, HttpServletResponse res) throws Exception {

    AbstractController.Parameter action = new AbstractController.Parameter("action", Integer.class);

    if (!checkParameters(req, res, Arrays.asList(new AbstractController.Parameter[]{action}))) return null;

    switch (action.getIntValue()) {
      case 0: // NEW
        newSavingsGoal(req, res);
        break;
      case 1: // EDIT
        editSavingsGoal(req, res);
        break;
      case 2: // GET
        getSavingsGoal(req, res);
        break;
      case 3: // LIST
        listSavingsGoals(res);
        break;
      case 4: // DELETE
        deleteSavingsGoal(req, res);
        break;
    }

    return null;
  }


  private void deleteSavingsGoal(HttpServletRequest req, HttpServletResponse res) throws IOException {
  }

  private void listSavingsGoals(HttpServletResponse res) throws IOException {
    /*
    Document doc = buildResponse();
    Element root = doc.getRootElement().addElement("bills");

    for (Envelope env : envelopeService.getBills(userService.findByUsername("nbolton"))) {
      envelopeService.buildEnvelope(env, root, "bill");
    }
    sendResponse(res, doc);
    */
  }

  private void getSavingsGoal(HttpServletRequest req, HttpServletResponse res) throws IOException {
  }

  private void editSavingsGoal(HttpServletRequest req, HttpServletResponse res) throws IOException {
  }

  private void newSavingsGoal(HttpServletRequest req, HttpServletResponse res) throws IOException {
  }
}
