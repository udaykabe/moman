package net.deuce.moman.controller;

import net.deuce.moman.om.EntityService;
import net.deuce.moman.om.PayeeService;
import net.deuce.moman.om.TagService;
import org.springframework.beans.factory.annotation.Autowired;

public class TagController extends DispatcherController {

  @Autowired
  private TagService tagService;

  protected EntityService getService() {
    return tagService;
  }
}