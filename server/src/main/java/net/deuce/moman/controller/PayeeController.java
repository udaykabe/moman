package net.deuce.moman.controller;

import net.deuce.moman.om.EntityService;
import net.deuce.moman.om.PayeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class PayeeController extends AbstractController {

  @Autowired
  private PayeeService payeeService;

  protected EntityService getService() {
    return payeeService;
  }
}
