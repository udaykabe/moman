package net.deuce.moman.operation;

import net.deuce.moman.model.AbstractEntity;
import net.deuce.moman.service.EntityService;

import org.eclipse.core.runtime.IProgressMonitor;


public class CreateEntityOperation<E extends AbstractEntity<E>, S extends EntityService<E>> extends CrudEntityOperation<E, S> {
	
	private E entity;

	public CreateEntityOperation(E entity, S service) {
		super(service);
		this.entity = entity;
	}

	@Override
	protected void doExecute(IProgressMonitor monitor) {
		getService().addEntity(entity, true);
	}

	@Override
	protected void doRedo(IProgressMonitor monitor) {
		getService().addEntity(entity, true);
	}

	@Override
	protected void doUndo(IProgressMonitor monitor) {
		getService().removeEntity(entity);
	}

}
