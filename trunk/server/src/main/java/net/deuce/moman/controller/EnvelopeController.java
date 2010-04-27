package net.deuce.moman.controller;

import net.deuce.moman.om.EnvelopeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class EnvelopeController extends AbstractController {

  @Autowired
  private EnvelopeService envelopeService;

  public ModelAndView handleRequest(HttpServletRequest req, HttpServletResponse res) throws Exception {

    handleDefaultActions(req, res, envelopeService);

    return null;
  }
}
