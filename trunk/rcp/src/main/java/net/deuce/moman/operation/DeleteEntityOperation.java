package net.deuce.moman.operation;

import java.util.List;

import net.deuce.moman.model.AbstractEntity;
import net.deuce.moman.service.EntityService;

import org.eclipse.core.runtime.IProgressMonitor;


public class DeleteEntityOperation<E extends AbstractEntity<E>, S extends EntityService<E>> extends CrudEntityOperation<E, S> {
	
	private List<E> entities;

	public DeleteEntityOperation(List<E> entities, S service) {
		super(service);
		this.entities = entities;
	}

	@Override
	protected void doExecute(IProgressMonitor monitor) {
		for (E entity : entities) {
			getService().removeEntity(entity);
		}
	}

	@Override
	protected void doRedo(IProgressMonitor monitor) {
		for (E entity : entities) {
			getService().removeEntity(entity);
		}
	}

	@Override
	protected void doUndo(IProgressMonitor monitor) {
		for (E entity : entities) {
			getService().addEntity(entity, true);
		}
	}

}
