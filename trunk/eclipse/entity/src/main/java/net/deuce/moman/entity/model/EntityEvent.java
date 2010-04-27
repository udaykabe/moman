package net.deuce.moman.entity.model;

import java.util.EventObject;

@SuppressWarnings("unchecked")
public class EntityEvent<E extends AbstractEntity> extends EventObject {
	
	private static final long serialVersionUID = 1L;
	
	private E entity;
	private EntityProperty property;
	
	public EntityEvent(Object source, EntityProperty property) {
		super(source);
		this.entity = (E)source;
		this.property = property;
	}

	public E getEntity() {
		return entity;
	}
	
	public EntityProperty getProperty() {
		return property;
	}
}
