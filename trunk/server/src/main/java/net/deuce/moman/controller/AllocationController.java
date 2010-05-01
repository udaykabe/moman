package net.deuce.moman.controller;

import net.deuce.moman.om.AccountService;
import net.deuce.moman.om.AllocationSetService;
import net.deuce.moman.om.EntityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class AllocationController extends AbstractController {

  @Autowired
  private AllocationSetService allocationSetService;

  protected EntityService getService() {
    return allocationSetService;
  }
}
