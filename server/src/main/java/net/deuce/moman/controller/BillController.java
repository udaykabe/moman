package net.deuce.moman.controller;

import net.deuce.moman.om.EntityService;
import net.deuce.moman.om.EnvelopeService;
import net.deuce.moman.om.UserService;
import org.springframework.beans.factory.annotation.Autowired;

public class BillController extends DispatcherController {

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
        newBill(req, res);
        break;
      case Actions.EDIT_ENTITY:
        editBill(req, res);
        break;
      case Actions.GET_ENTITY:
        getBill(req, res);
        break;
      case Actions.LIST_ENTITIES:
        listBills(res);
        break;
      case Actions.DELETE_ENTITY:
        deleteBill(req, res);
        break;
    }

    return null;
  }


  private void deleteBill(HttpServletRequest req, HttpServletResponse res) throws IOException {
  }

  private void listBills(HttpServletResponse res) throws IOException {
    Document doc = buildResponse();
    Element root = doc.getRootElement().addElement("bills");

    for (Envelope env : envelopeService.getBills(userService.findByUsername("nbolton"))) {
      envelopeService.buildEnvelope(env, root, "bill");
    }
    sendResponse(res, doc);
  }

  private void getBill(HttpServletRequest req, HttpServletResponse res) throws IOException {
  }

  private void editBill(HttpServletRequest req, HttpServletResponse res) throws IOException {
  }

  private void newBill(HttpServletRequest req, HttpServletResponse res) throws IOException {
  }
  */
}
