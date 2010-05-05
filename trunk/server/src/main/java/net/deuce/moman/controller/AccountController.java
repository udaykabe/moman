package net.deuce.moman.controller;

import net.deuce.moman.om.*;
import org.springframework.beans.factory.annotation.Autowired;

public class AccountController extends DispatcherController {

  @Autowired
  private AccountService accountService;

  @Autowired
  private EnvelopeService envelopeService;

  @Autowired
  private TransactionService transactionService;

  @Autowired
  private RuleService ruleService;

  @Autowired
  private FinancialInstitutionService financialInstitutionService;


  protected EntityService getService() {
    return accountService;
  }

  /*
  public void handleActions(HttpServletRequest req, HttpServletResponse res) throws Exception {

    Parameter.Parameter action = new Parameter.Parameter("action", Integer.class);
    Parameter.Parameter uuid;
    Parameter.Parameter force;
    List<Parameter.Parameter> params = new LinkedList<Parameter.Parameter>();
    params.add(action);

    if (!checkParameters(req, res, params)) return;

    switch (action.getIntValue()) {
      case Actions.NEW_DISCONNECTED_ACCOUNT:
        newDisconnectedAccount(req, res);
        break;
      case Actions.IMPORT_ACCOUNT_TRANSACTIONS:
        uuid = new Parameter.Parameter("uuid", String.class);
        params.add(uuid);
        force = new Parameter.Parameter("force", Boolean.class);
        params.add(force);
        if (!checkParameters(req, res, params)) return;
        sendResult(importAccountTransactions(uuid.getValue(), force.getBoolValue()), res);
        break;
    }
  }

  private Result importAccountTransactions(String uuid, boolean force) throws Exception {
    Account account = accountService.findEntity(uuid);
    FinancialInstitutionImportTransactionCommand command = new FinancialInstitutionImportTransactionCommand(account, force);
    command.setEnvelopeService(envelopeService);
    command.setTransactionService(transactionService);
    command.setRuleService(ruleService);
    command.setFinancialInstitutionService(financialInstitutionService);
    command.setAccountService(accountService);
    return getUndoManager().execute(account.getUser(), command, null);
  }

  private void newDisconnectedAccount(HttpServletRequest req, HttpServletResponse res) throws IOException {
  }
  */
}
