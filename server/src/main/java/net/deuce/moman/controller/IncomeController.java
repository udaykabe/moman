package net.deuce.moman.controller;

import net.deuce.moman.om.EntityService;
import net.deuce.moman.om.IncomeService;
import org.springframework.beans.factory.annotation.Autowired;

public class IncomeController extends DispatcherController {

  @Autowired
  private IncomeService incomeService;

  protected EntityService getService() {
    return incomeService;
  }
}
