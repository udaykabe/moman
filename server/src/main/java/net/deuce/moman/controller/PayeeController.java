package net.deuce.moman.controller;

import net.deuce.moman.om.PayeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class PayeeController extends AbstractController {

  @Autowired
  private PayeeService payeeService;

  public ModelAndView handleRequest(HttpServletRequest req, HttpServletResponse res) throws Exception {

    handleDefaultActions(req, res, payeeService);

    return null;
  }
}
