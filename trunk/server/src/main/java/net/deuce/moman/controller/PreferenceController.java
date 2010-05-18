package net.deuce.moman.controller;

import net.deuce.moman.om.EntityService;
import net.deuce.moman.om.PreferenceService;
import org.springframework.beans.factory.annotation.Autowired;

public class PreferenceController extends DispatcherController {

  @Autowired
  private PreferenceService preferenceService;

  protected EntityService getService() {
    return preferenceService;
  }
}