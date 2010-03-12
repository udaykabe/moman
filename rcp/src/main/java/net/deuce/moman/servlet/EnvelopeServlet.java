package net.deuce.moman.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.deuce.moman.entity.ServiceProvider;
import net.deuce.moman.entity.model.envelope.Envelope;
import net.deuce.moman.entity.model.envelope.EnvelopeBuilder;
import net.deuce.moman.entity.service.envelope.EnvelopeService;

public class EnvelopeServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	private EnvelopeService envelopeService = ServiceProvider.instance().getEnvelopeService();

	private EnvelopeBuilder envelopeBuilder = ServiceProvider.instance().getEnvelopeBuilder();

	public EnvelopeServlet() {
	}

	protected void doGet(HttpServletRequest req, HttpServletResponse res)
			throws ServletException, IOException {

		String[] path = null;
		String pathInfo = req.getPathInfo();
		if (pathInfo != null) {
			path = req.getPathInfo().split("/");
		}

		Envelope env = envelopeService.getRootEnvelope();

		for (int i = 1; path != null && i < path.length; i++) {
			env = env.getChild(path[i]);
			if (env == null) {
				res.setStatus(HttpServletResponse.SC_NOT_FOUND);
				return;
			}
		}

		envelopeBuilder.printEntities(res.getWriter(), env);
	}

}
