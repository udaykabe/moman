package net.deuce.moman.controller;

import net.deuce.moman.om.AccountService;
import net.deuce.moman.om.EntityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class AccountController extends AbstractController {

  @Autowired
  private AccountService accountService;

  protected EntityService getService() {
    return accountService;
  }

  public void handleActions(HttpServletRequest req, HttpServletResponse res) throws IOException {

    Parameter action = new Parameter("action", Integer.class);

    switch (action.getIntValue()) {
      case 8: // NEW_DISCONNECTED
        newDisconnectedAccount(req, res);
        break;
    }
  }
  
  private void newDisconnectedAccount(HttpServletRequest req, HttpServletResponse res) throws IOException {
  }
}
