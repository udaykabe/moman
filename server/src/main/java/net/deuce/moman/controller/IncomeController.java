package net.deuce.moman.controller;

import net.deuce.moman.om.EntityService;
import net.deuce.moman.om.IncomeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class IncomeController extends AbstractController {

  @Autowired
  private IncomeService incomeService;

  protected EntityService getService() {
    return incomeService;
  }
}
