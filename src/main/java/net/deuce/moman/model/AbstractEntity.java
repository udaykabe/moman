package net.deuce.moman.model;

import java.util.Comparator;

import net.deuce.moman.operation.EntitySetterOperation;
import net.deuce.moman.service.ServiceNeeder;
import net.deuce.moman.ui.Activator;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.IOperationHistory;
import org.eclipse.core.commands.operations.IUndoContext;
import org.eclipse.core.commands.operations.IUndoableOperation;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.ui.IWorkbench;

@SuppressWarnings("unchecked")
public abstract class AbstractEntity<E extends AbstractEntity> extends ServiceNeeder
implements Comparator<E>, Comparable<E> {

	private String id;
	
	private transient Comparator<E> forwardComparator;
	private transient Comparator<E> reverseComparator;
	private transient EntityMonitor<E> monitor = new EntityMonitor<E>();
	
	public AbstractEntity() {
	}
	
	public boolean evaluateBoolean(Boolean b) {
		return b != null && b;
	}
	
	public EntityMonitor<E> getMonitor() {
		return monitor;
	}

	public void setMonitor(EntityMonitor<E> monitor) {
		this.monitor = monitor;
	}

	public Comparator<E> getForwardComparator() {
		if (forwardComparator == null) {
			forwardComparator = new Comparator<E>() {
				@Override
				public int compare(E o1, E o2) {
					return AbstractEntity.this.compare(o1, o2);
				}
			};
		}
		return forwardComparator;
	}
	
	public Comparator<E> getReverseComparator() {
		if (reverseComparator == null) {
			reverseComparator = new Comparator<E>() {
				@Override
				public int compare(E o1, E o2) {
					return -AbstractEntity.this.compare(o1, o2);
				}
			};
		}
		return reverseComparator;
	}
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	@Override
	public String toString() {
		return String.format("%s %s", getClass().getSimpleName(), id);
	}
	
	protected boolean propertyChanged(Object s1, Object s2) {
		if (s1 == s2) return false;
		return s1 != s2 && ( (s1 != null && !s1.equals(s2)) || (s2 != null && !s2.equals(s1)) );
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AbstractEntity other = (AbstractEntity) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
	
	public void executeChange(EntityProperty property, Object value) {
		EntitySetterOperation<E> operation = new EntitySetterOperation<E>((E) this, property, value);
		executeOperation(operation);
	}
	
	protected void executeOperation(IUndoableOperation operation) {
		
		IWorkbench workbench = Activator.getDefault().getWorkbench();
		IOperationHistory operHistory = 
		workbench.getOperationSupport().getOperationHistory();
		IUndoContext myContext = 
		workbench.getOperationSupport().getUndoContext();
		operation.addContext(myContext);
		try {
			operHistory.execute(operation, new NullProgressMonitor(), null);
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
	}
}
