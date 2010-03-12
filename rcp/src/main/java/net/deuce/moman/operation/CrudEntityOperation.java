package net.deuce.moman.operation;

import net.deuce.moman.entity.model.AbstractEntity;
import net.deuce.moman.entity.service.EntityService;

public abstract class CrudEntityOperation<E extends AbstractEntity<E>, S extends EntityService<E>>
		extends GenericOperation {

	private S service;

	public CrudEntityOperation(S service) {
		this.service = service;
	}

	protected S getService() {
		return service;
	}
}
