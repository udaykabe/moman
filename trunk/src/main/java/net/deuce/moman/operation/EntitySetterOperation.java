package net.deuce.moman.operation;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;

import net.deuce.moman.model.AbstractEntity;
import net.deuce.moman.model.EntityProperty;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.AbstractOperation;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

@SuppressWarnings("unchecked")
public class EntitySetterOperation<E extends AbstractEntity> extends AbstractOperation {
	
	private E entity;
	private EntityProperty property;
	private Object oldValue;
	private Object newValue;

	public EntitySetterOperation(E entity, EntityProperty property, Object newValue) {
		super("");
		setLabel(((Class<E>)((ParameterizedType) entity.getClass().getGenericSuperclass()).getActualTypeArguments()[0]).getSimpleName() + " Setter");
		
		if (entity == null) {
			throw new RuntimeException("Missing parameter 'entity'");
		}

		if (property == null) {
			throw new RuntimeException("Missing parameter 'property'");
		}

		this.entity = entity;
		this.property = property;
		this.newValue = newValue;
	}
	
	private String buildMethodName(String type, String s) {
		StringBuffer sb = new StringBuffer(type);
		sb.append(s.substring(0,1).toUpperCase());
		sb.append(s.substring(1));
		return sb.toString();
	}

	@Override
	public IStatus execute(IProgressMonitor monitor, IAdaptable info)
			throws ExecutionException {
		try {
			
			Method getter = entity.getClass().getDeclaredMethod(buildMethodName("get", property.name()));
			oldValue = getter.invoke(entity, new Object[0]);
			
			Method setter = entity.getClass().getDeclaredMethod(buildMethodName("set", property.name()), property.type());
			setter.invoke(entity, newValue);
			return Status.OK_STATUS;
		} catch (Throwable t) {
			t.printStackTrace();
			return new Status(Status.ERROR, "", t.getMessage(), t);
		}
	}

	@Override
	public IStatus redo(IProgressMonitor monitor, IAdaptable info)
			throws ExecutionException {
		try {
			Method setter = entity.getClass().getDeclaredMethod(buildMethodName("set", property.name()), property.type());
			setter.invoke(entity, newValue);
			return Status.OK_STATUS;
		} catch (Throwable t) {
			t.printStackTrace();
			return new Status(Status.ERROR, "", t.getMessage(), t);
		}
	}

	@Override
	public IStatus undo(IProgressMonitor monitor, IAdaptable info)
			throws ExecutionException {
		try {
			Method setter = entity.getClass().getDeclaredMethod(buildMethodName("set", property.name()), property.type());
			setter.invoke(entity, oldValue);
			return Status.OK_STATUS;
		} catch (Throwable t) {
			t.printStackTrace();
			return new Status(Status.ERROR, "", t.getMessage(), t);
		}
	}

}
