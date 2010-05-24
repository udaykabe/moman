package net.deuce.moman.controller;

import net.deuce.moman.om.EntityService;
import net.deuce.moman.om.UserService;
import org.springframework.beans.factory.annotation.Autowired;

public class UserController extends DispatcherController {

  @Autowired
  private UserService userService;

  protected EntityService getService() {
    return userService;
  }
}