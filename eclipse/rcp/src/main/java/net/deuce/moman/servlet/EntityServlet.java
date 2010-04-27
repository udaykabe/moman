package net.deuce.moman.servlet;

import java.io.IOException;
import java.util.Arrays;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.deuce.moman.entity.model.AbstractBuilder;
import net.deuce.moman.entity.model.AbstractEntity;
import net.deuce.moman.entity.service.EntityService;

@SuppressWarnings("unchecked")
public abstract class EntityServlet<E extends AbstractEntity, S extends EntityService, B extends AbstractBuilder>
		extends HttpServlet {

	private static final long serialVersionUID = 1L;

	private S service;
	private B builder;

	public EntityServlet() {
		initialize();
	}

	protected abstract void initialize();

	public S getService() {
		return service;
	}

	public void setService(S service) {
		this.service = service;
	}

	public B getBuilder() {
		return builder;
	}

	public void setBuilder(B builder) {
		this.builder = builder;
	}

	protected void doGet(HttpServletRequest req, HttpServletResponse res)
			throws ServletException, IOException {

		String id = req.getParameter("id");

		AbstractEntity entity = service.findEntity(id);

		if (entity == null) {
			res.setStatus(HttpServletResponse.SC_NOT_FOUND);
			return;
		}

		builder.printEntities(res.getWriter(), Arrays
				.asList(new AbstractEntity[] { entity }));
	}

}
