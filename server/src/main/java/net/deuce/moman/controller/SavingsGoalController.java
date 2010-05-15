package net.deuce.moman.controller;

import net.deuce.moman.om.EntityService;
import net.deuce.moman.om.EnvelopeService;
import net.deuce.moman.om.UserService;
import org.springframework.beans.factory.annotation.Autowired;

public class SavingsGoalController extends DispatcherController {

  @Autowired
  private EnvelopeService envelopeService;

  @Autowired
  private UserService userService;

  protected EntityService getService() {
    return envelopeService;
  }

  /*
  public ModelAndView handleRequest(HttpServletRequest req, HttpServletResponse res) throws Exception {

    Parameter.Parameter action = new Parameter.Parameter("action", Integer.class);

    if (!checkParameters(req, res, Arrays.asList(new Parameter.Parameter[]{action}))) return null;

    switch (action.getIntValue()) {
      case Actions.NEW_ENTITY:
        newSavingsGoal(req, res);
        break;
      case Actions.EDIT_ENTITY:
        editSavingsGoal(req, res);
        break;
      case Actions.GET_ENTITY:
        getSavingsGoal(req, res);
        break;
      case Actions.LIST_ENTITIES:
        listSavingsGoals(res);
        break;
      case Actions.DELETE_ENTITY:
        deleteSavingsGoal(req, res);
        break;
    }

    return null;
  }


  private void deleteSavingsGoal(HttpServletRequest req, HttpServletResponse res) throws IOException {
  }

  private void listSavingsGoals(HttpServletResponse res) throws IOException {
    Document doc = buildResponse();
    Element root = doc.getRootElement().addElement("bills");

    for (Envelope env : envelopeService.getBills(userService.getDefaultUser())) {
      envelopeService.buildEnvelope(env, root, "bill");
    }
    sendResponse(res, doc);
  }

  private void getSavingsGoal(HttpServletRequest req, HttpServletResponse res) throws IOException {
  }

  private void editSavingsGoal(HttpServletRequest req, HttpServletResponse res) throws IOException {
  }

  private void newSavingsGoal(HttpServletRequest req, HttpServletResponse res) throws IOException {
  }
    */
}
