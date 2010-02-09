package net.deuce.moman.model;

import java.util.LinkedList;
import java.util.List;

public class EntityMonitor<E extends MomanEntity> {

	private List<EntityListener<E>> listeners = new LinkedList<EntityListener<E>>();
	private boolean monitoring = true;
	
	public boolean isMonitoring() {
		return monitoring;
	}

	public void setMonitoring(boolean monitoring) {
		this.monitoring = monitoring;
	}

	public void addListener(EntityListener<E> listener) {
		listeners.add(listener);
	}
	
	public void removeListener(EntityListener<E> listener) {
		listeners.remove(listener);
	}
	
	private EntityEvent<E> createEvent(E entity) {
		EntityEvent<E> event = null;
		if (entity != null) {
			event = new EntityEvent<E>(entity);
		}
		return event;
	}
	
	public void fireEntityAdded(E entity) {
		if (monitoring) {
			EntityEvent<E> event = createEvent(entity);
			for (EntityListener<E> listener : listeners) {
				listener.entityAdded(event);
			}
		}
	}
	
	public void fireEntityRemoved(E entity) {
		if (monitoring) {
			EntityEvent<E> event = createEvent(entity);
			for (EntityListener<E> listener : listeners) {
				listener.entityRemoved(event);
			}
		}
	}
	
	public void fireEntityChanged(E entity) {
		if (monitoring) {
			EntityEvent<E> event = createEvent(entity);
			for (EntityListener<E> listener : listeners) {
				listener.entityChanged(event);
			}
		}
	}
}
