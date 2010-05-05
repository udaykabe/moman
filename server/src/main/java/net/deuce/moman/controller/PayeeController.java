package net.deuce.moman.controller;

import net.deuce.moman.om.EntityService;
import net.deuce.moman.om.PayeeService;
import org.springframework.beans.factory.annotation.Autowired;

public class PayeeController extends DispatcherController {

  @Autowired
  private PayeeService payeeService;

  protected EntityService getService() {
    return payeeService;
  }
}
