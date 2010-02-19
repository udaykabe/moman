package net.deuce.moman.model;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

@SuppressWarnings("unchecked")
public class EntityMonitor<E extends AbstractEntity> {

	private List<EntityListener<E>> listeners = new LinkedList<EntityListener<E>>();
	private boolean queuingNotifications;
	private Set<E> removedEntities = new HashSet<E>();
	private Set<E> addedEntities = new HashSet<E>();
	private Set<ChangedEntity<E>> changedEntities = new HashSet<ChangedEntity<E>>();
	private boolean singleChange = false;
	private boolean processing = false;
	
	public boolean isQueuingNotifications() {
		return queuingNotifications;
	}

	public void setQueuingNotifications(boolean queuingNotifications) {
		
		if (processing) return;
		
		processing = true;
		
		try {
			this.queuingNotifications = queuingNotifications;
			if (!queuingNotifications) {
				
				for (E entity : addedEntities) {
					fireEntityAdded(entity);
				}
				for (E entity : removedEntities) {
					fireEntityRemoved(entity);
				}
				for (ChangedEntity<E> changedEntity : changedEntities) {
					fireEntityChanged(changedEntity.entity, changedEntity.property);
					if (singleChange) break;
				}
			}
			removedEntities.clear();
			addedEntities.clear();
			changedEntities.clear();
		} finally {
			processing = true;
		}
	}
	
	public boolean isSingleChange() {
		return singleChange;
	}

	public void setSingleChange(boolean singleChange) {
		this.singleChange = singleChange;
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
	
	public void fireEntityAdded(E entity, EntityProperty property) {
		if (!queuingNotifications) {
			EntityEvent<E> event = createEvent(entity, property);
			for (EntityListener<E> listener : listeners) {
				listener.entityAdded(event);
			}
		} else {
			addedEntities.add(entity);
		}
	}
	
	public void fireEntityAdded(E entity) {
		fireEntityAdded(entity, null);
	}
	
	public void fireEntityRemoved(E entity) {
		if (!queuingNotifications) {
			EntityEvent<E> event = createEvent(entity, null);
			for (EntityListener<E> listener : listeners) {
				listener.entityRemoved(event);
			}
		} else {
			removedEntities.add(entity);
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
			changedEntities.add(new ChangedEntity<E>(entity, property));
		}
	}
	
	private static class ChangedEntity<E extends AbstractEntity> {
		public E entity;
		public EntityProperty property;
		public ChangedEntity(E entity, EntityProperty property) {
			super();
			this.entity = entity;
			this.property = property;
		}
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result
					+ ((entity == null) ? 0 : entity.hashCode());
			result = prime * result
					+ ((property == null) ? 0 : property.hashCode());
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
			ChangedEntity other = (ChangedEntity) obj;
			if (entity == null) {
				if (other.entity != null)
					return false;
			} else if (!entity.equals(other.entity))
				return false;
			if (property == null) {
				if (other.property != null)
					return false;
			} else if (!property.equals(other.property))
				return false;
			return true;
		}
		
	}
}
