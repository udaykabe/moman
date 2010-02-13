package net.deuce.moman.model;

import java.util.LinkedList;
import java.util.List;

@SuppressWarnings("unchecked")
public class EntityMonitor<E extends AbstractEntity> {

	private List<EntityListener<E>> listeners = new LinkedList<EntityListener<E>>();
	private boolean queuingNotifications;
	private boolean needingToNotifyRemovals;
	private boolean needingToNotifyAdditions;
	private boolean needingToNotifyChanges;
	
	public boolean isQueuingNotifications() {
		return queuingNotifications;
	}

	public void setQueuingNotifications(boolean queuingNotifications) {
		this.queuingNotifications = queuingNotifications;
		if (queuingNotifications) {
			needingToNotifyAdditions = false;
			needingToNotifyRemovals = false;
			needingToNotifyChanges = false;
		} else {
			if (needingToNotifyAdditions) {
				fireEntityAdded(null);
			}
			if (needingToNotifyRemovals) {
				fireEntityRemoved(null);
			}
			if (needingToNotifyChanges) {
				fireEntityChanged(null);
			}
		}
	}
	
	public void addListener(EntityListener<E> listener) {
		listeners.add(listener);
	}
	
	public void removeListener(EntityListener<E> listener) {
		listeners.remove(listener);
	}
	
	private EntityEvent<E> createEvent(E entity, EntityProperty property) {
		EntityEvent<E> event = null;
		if (entity != null) {
			event = new EntityEvent<E>(entity, property);
		}
		return event;
	}
	
	public void fireEntityAdded(E entity) {
		if (!queuingNotifications) {
			EntityEvent<E> event = createEvent(entity, null);
			for (EntityListener<E> listener : listeners) {
				listener.entityAdded(event);
			}
		} else {
			needingToNotifyAdditions = true;
		}
	}
	
	public void fireEntityRemoved(E entity) {
		if (!queuingNotifications) {
			EntityEvent<E> event = createEvent(entity, null);
			for (EntityListener<E> listener : listeners) {
				listener.entityRemoved(event);
			}
		} else {
			needingToNotifyRemovals = true;
		}
	}
	
	public void fireEntityChanged(E entity) {
		fireEntityChanged(entity, null);
	}
	
	public void fireEntityChanged(E entity, EntityProperty property) {
		if (!queuingNotifications) {
			EntityEvent<E> event = createEvent(entity, property);
			for (EntityListener<E> listener : listeners) {
				listener.entityChanged(event);
			}
		} else {
			needingToNotifyChanges = true;
		}
	}
}
