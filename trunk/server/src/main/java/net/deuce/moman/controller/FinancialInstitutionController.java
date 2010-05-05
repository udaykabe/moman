package net.deuce.moman.controller;

import net.deuce.moman.om.EntityService;
import net.deuce.moman.om.FinancialInstitutionService;
import org.springframework.beans.factory.annotation.Autowired;

public class FinancialInstitutionController extends DispatcherController {

  @Autowired
  private FinancialInstitutionService financialInstitutionService;

  protected EntityService getService() {
    return financialInstitutionService;
  }
}
