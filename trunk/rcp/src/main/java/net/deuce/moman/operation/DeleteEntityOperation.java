package net.deuce.moman.operation;

import java.util.List;

import net.deuce.moman.entity.model.AbstractEntity;
import net.deuce.moman.entity.service.EntityService;

import org.eclipse.core.runtime.IProgressMonitor;

public class DeleteEntityOperation<E extends AbstractEntity<E>, S extends EntityService<E>>
		extends CrudEntityOperation<E, S> {

	private List<E> entities;

	public DeleteEntityOperation(List<E> entities, S service) {
		super(service);
		this.entities = entities;
	}

	protected void doExecute(IProgressMonitor monitor) {
		for (E entity : entities) {
			getService().removeEntity(entity);
		}
	}

	protected void doRedo(IProgressMonitor monitor) {
		for (E entity : entities) {
			getService().removeEntity(entity);
		}
	}

	protected void doUndo(IProgressMonitor monitor) {
		for (E entity : entities) {
			getService().addEntity(entity, true);
		}
	}

}
