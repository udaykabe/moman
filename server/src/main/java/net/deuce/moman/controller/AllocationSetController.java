package net.deuce.moman.controller;

import net.deuce.moman.om.AllocationSetService;
import net.deuce.moman.om.EntityService;
import org.springframework.beans.factory.annotation.Autowired;

public class AllocationSetController extends DispatcherController {

  @Autowired
  private AllocationSetService allocationSetService;

  protected EntityService getService() {
    return allocationSetService;
  }
}