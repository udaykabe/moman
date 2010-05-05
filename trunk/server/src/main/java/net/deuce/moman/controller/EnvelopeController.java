package net.deuce.moman.controller;

import net.deuce.moman.om.EntityService;
import net.deuce.moman.om.EnvelopeService;
import org.springframework.beans.factory.annotation.Autowired;

public class EnvelopeController extends DispatcherController {

  @Autowired
  private EnvelopeService envelopeService;

  protected EntityService getService() {
    return envelopeService;
  }
}
