package net.deuce.moman.controller;

import net.deuce.moman.om.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class TransactionController extends AbstractController {

  @Autowired
  private TransactionService tranactionService;

  public ModelAndView handleRequest(HttpServletRequest req, HttpServletResponse res) throws Exception {

    handleDefaultActions(req, res, tranactionService);

    return null;
  }
}
