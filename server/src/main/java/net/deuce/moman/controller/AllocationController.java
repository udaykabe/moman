package net.deuce.moman.controller;

import net.deuce.moman.om.AllocationService;
import net.deuce.moman.om.AllocationSetService;
import net.deuce.moman.om.EntityService;
import org.springframework.beans.factory.annotation.Autowired;

public class AllocationController extends DispatcherController {

  @Autowired
  private AllocationService allocationService;

  protected EntityService getService() {
    return allocationService;
  }
}
