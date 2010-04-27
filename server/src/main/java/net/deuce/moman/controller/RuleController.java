package net.deuce.moman.controller;

import net.deuce.moman.om.RuleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class RuleController extends AbstractController {

  @Autowired
  private RuleService ruleService;

  public ModelAndView handleRequest(HttpServletRequest req, HttpServletResponse res) throws Exception {

    handleDefaultActions(req, res, ruleService);

    return null;
  }
}
