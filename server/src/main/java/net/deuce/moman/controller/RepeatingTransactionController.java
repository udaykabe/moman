package net.deuce.moman.controller;

import net.deuce.moman.om.EntityService;
import net.deuce.moman.om.RepeatingTransactionService;
import org.springframework.beans.factory.annotation.Autowired;

public class RepeatingTransactionController extends DispatcherController {

  @Autowired
  private RepeatingTransactionService repeatingTransactionService;

  protected EntityService getService() {
    return repeatingTransactionService;
  }
}
