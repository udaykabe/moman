package net.deuce.moman.controller;

import net.deuce.moman.om.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class AccountController extends AbstractController {

  @Autowired
  private AccountService accountService;

  public ModelAndView handleRequest(HttpServletRequest req, HttpServletResponse res) throws Exception {

    if (handleDefaultActions(req, res, accountService)) return null;

    Parameter action = new Parameter("action", Integer.class);

    switch (action.getIntValue()) {
      case 5: // NEW_DISCONNECTED
        newDisconnectedAccount(req, res);
        break;
    }

    return null;
  }
  
  private void newDisconnectedAccount(HttpServletRequest req, HttpServletResponse res) throws IOException {
  }
}
