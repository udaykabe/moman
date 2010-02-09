package net.deuce.moman.model;

import java.util.EventObject;

public class EntityEvent<E extends MomanEntity> extends EventObject {
	
	private static final long serialVersionUID = 1L;
	
	private E entity;
	
	@SuppressWarnings("unchecked")
	public EntityEvent(Object source) {
		super(source);
		entity = (E)source;
	}

	public E getEntity() {
		return entity;
	}
}
