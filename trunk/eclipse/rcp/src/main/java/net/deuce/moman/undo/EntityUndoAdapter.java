package net.deuce.moman.undo;

import net.deuce.moman.entity.model.AbstractEntity;
import net.deuce.moman.entity.model.EntityProperty;
import net.deuce.moman.operation.EntitySetterOperation;
import net.deuce.moman.ui.Activator;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.IOperationHistory;
import org.eclipse.core.commands.operations.IUndoContext;
import org.eclipse.core.commands.operations.IUndoableOperation;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.ui.IWorkbench;

@SuppressWarnings("unchecked")
public class EntityUndoAdapter<E extends AbstractEntity> implements UndoAdapter {

	private E entity;

	public EntityUndoAdapter(E entity) {
		this.entity = entity;
	}

	public E getEntity() {
		return entity;
	}

	public void executeChange(EntityProperty property, Object value) {
		EntitySetterOperation<E> operation = new EntitySetterOperation<E>(
				entity, property, value);
		executeOperation(operation);
	}

	protected void executeOperation(IUndoableOperation operation) {

		IWorkbench workbench = Activator.getDefault().getWorkbench();
		IOperationHistory operHistory = workbench.getOperationSupport()
				.getOperationHistory();
		IUndoContext myContext = workbench.getOperationSupport()
				.getUndoContext();
		operation.addContext(myContext);
		try {
			operHistory.execute(operation, new NullProgressMonitor(), null);
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
	}

}
