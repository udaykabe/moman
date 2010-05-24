package net.deuce.moman.controller;

import net.deuce.moman.om.AlertService;
import net.deuce.moman.om.EntityService;
import org.springframework.beans.factory.annotation.Autowired;

public class AlertController extends DispatcherController {

  @Autowired
  private AlertService alertService;

  protected EntityService getService() {
    return alertService;
  }
}