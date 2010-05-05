package net.deuce.moman.controller;

import net.deuce.moman.om.EntityService;
import net.deuce.moman.om.RuleService;
import org.springframework.beans.factory.annotation.Autowired;

public class RuleController extends DispatcherController {

  @Autowired
  private RuleService ruleService;

  protected EntityService getService() {
    return ruleService;
  }
}
