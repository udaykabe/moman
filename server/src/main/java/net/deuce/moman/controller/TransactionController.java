package net.deuce.moman.controller;

import net.deuce.moman.om.EntityService;
import net.deuce.moman.om.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;

public class TransactionController extends DispatcherController {

  @Autowired
  private TransactionService transactionService;

  protected EntityService getService() {
    return transactionService;
  }

  /*
  public void handleActions(HttpServletRequest req, HttpServletResponse res) throws Exception {

    Parameter.Parameter action = new Parameter.Parameter("action", Integer.class);
    Parameter.Parameter reverse;
    List<Parameter.Parameter> params = new LinkedList<Parameter.Parameter>();
    params.add(action);

    if (!checkParameters(req, res, params)) return;

    switch (action.getIntValue()) {
      case Actions.LIST_CUSTOM_TRANSACTIONS:
        reverse = new Parameter.Parameter("reverse", Boolean.class);
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
  */
}
