package net.deuce.moman.controller;

import net.deuce.moman.om.EntityService;
import net.deuce.moman.om.FinancialInstitutionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class FinancialInstitutionController extends AbstractController {

  @Autowired
  private FinancialInstitutionService financialInstitutionService;

  protected EntityService getService() {
    return financialInstitutionService;
  }
}
