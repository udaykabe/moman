package net.deuce.moman.controller;

import net.deuce.moman.om.EntityService;
import net.deuce.moman.om.InternalTransaction;
import net.deuce.moman.om.TransactionService;
import org.dom4j.Document;
import org.dom4j.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class TransactionController extends AbstractController {

  @Autowired
  private TransactionService transactionService;

  protected EntityService getService() {
    return transactionService;
  }

  public void handleActions(HttpServletRequest req, HttpServletResponse res) throws Exception {

    Parameter action = new Parameter("action", Integer.class);
    Parameter reverse;
    List<Parameter> params = new LinkedList<Parameter>();
    params.add(action);

    if (!checkParameters(req, res, params)) return;

    switch (action.getIntValue()) {
      case Actions.LIST_CUSTOM_TRANSACTIONS:
        reverse = new Parameter("reverse", Boolean.class);
        params.add(reverse);
        if (!checkParameters(req, res, params)) return;
        listCustomTransactions(reverse.getBoolValue(), res);
        break;
    }
  }

  private void listCustomTransactions(boolean reverse, HttpServletResponse res) throws IOException {
    Document doc = buildResponse();
    Element root = doc.getRootElement().addElement("transactions");

    for (InternalTransaction trans : transactionService.getCustomTransactions(getUserService().getStaticUser(), reverse)) {
      transactionService.toXml(trans, root);
    }
    sendResponse(res, doc);
  }
}
