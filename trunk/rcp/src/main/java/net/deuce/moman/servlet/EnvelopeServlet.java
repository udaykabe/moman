package net.deuce.moman.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.deuce.moman.envelope.model.Envelope;
import net.deuce.moman.envelope.model.EnvelopeBuilder;
import net.deuce.moman.envelope.service.EnvelopeService;
import net.deuce.moman.service.ServiceNeeder;

public class EnvelopeServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	
	private EnvelopeService envelopeService;
	private EnvelopeBuilder envelopeBuilder;

	public EnvelopeServlet() {
		envelopeService = ServiceNeeder.instance().getEnvelopeService();
		envelopeBuilder = new EnvelopeBuilder();
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse res)
			throws ServletException, IOException {
		
		String[] path = null;
		String pathInfo = req.getPathInfo();
		if (pathInfo != null) {
			path = req.getPathInfo().split("/");
		}
		
		Envelope env = envelopeService.getRootEnvelope();
		
		for (int i=1; path != null && i<path.length; i++) {
			env = env.getChild(path[i]);
			if (env == null) {
				res.setStatus(HttpServletResponse.SC_NOT_FOUND);
				return;
			}
		}
		
		envelopeBuilder.printEntities(res.getWriter(), env);
	}

}
