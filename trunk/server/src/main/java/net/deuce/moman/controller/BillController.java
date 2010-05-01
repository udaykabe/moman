package net.deuce.moman.controller;

import net.deuce.moman.om.EntityService;
import net.deuce.moman.om.Envelope;
import net.deuce.moman.om.EnvelopeService;
import net.deuce.moman.om.UserService;
import org.dom4j.Document;
import org.dom4j.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;

public class BillController extends AbstractController {

  @Autowired
  private EnvelopeService envelopeService;

  @Autowired
  private UserService userService;

  protected EntityService getService() {
    return envelopeService;
  }

  public ModelAndView handleRequest(HttpServletRequest req, HttpServletResponse res) throws Exception {

    Parameter action = new Parameter("action", Integer.class);

    if (!checkParameters(req, res, Arrays.asList(new Parameter[]{action}))) return null;

    switch (action.getIntValue()) {
      case 0: // NEW
        newBill(req, res);
        break;
      case 1: // EDIT
        editBill(req, res);
        break;
      case 2: // GET
        getBill(req, res);
        break;
      case 3: // LIST
        listBills(res);
        break;
      case 4: // DELETE
        deleteBill(req, res);
        break;
    }

    return null;
  }


  private void deleteBill(HttpServletRequest req, HttpServletResponse res) throws IOException {
  }

  private void listBills(HttpServletResponse res) throws IOException {
    /*
    Document doc = buildResponse();
    Element root = doc.getRootElement().addElement("bills");

    for (Envelope env : envelopeService.getBills(userService.findByUsername("nbolton"))) {
      envelopeService.buildEnvelope(env, root, "bill");
    }
    sendResponse(res, doc);
    */
  }

  private void getBill(HttpServletRequest req, HttpServletResponse res) throws IOException {
  }

  private void editBill(HttpServletRequest req, HttpServletResponse res) throws IOException {
  }

  private void newBill(HttpServletRequest req, HttpServletResponse res) throws IOException {
  }
}
