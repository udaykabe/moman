package net.deuce.moman.entity.service;

import java.util.LinkedList;
import java.util.List;

import net.deuce.moman.entity.model.AbstractEntity;
import net.deuce.moman.entity.model.EntityListener;
import net.deuce.moman.entity.model.EntityMonitor;
import net.deuce.moman.entity.model.EntityProperty;

@SuppressWarnings("unchecked")
public interface EntityService<E extends AbstractEntity> {

	public boolean isSingleChange();

	public void setSingleChange(boolean singleChange);
	
	public boolean isQueuingNotifications();
	
	public String startQueuingNotifications();
	
	public void stopQueuingNotifications(String id);
	
	public E getAddedEntity();

	public void setAddedEntity(E addedEntity);

	public LinkedList<E> getEntities();
	
	public boolean isClearable();
	
	public List<E> getOrderedEntities(boolean reverse);
	
	public boolean entityExists(String id);
	
	public E findEntity(String id);
	
	public E getEntity(String id);
	
	public void addEntity(E entity);
	
	public void addEntity(E entity, boolean notify);
	
	public void removeEntity(E entity);
	
	public void setEntities(List<E> entities);
	
	public void clearEntities();
	
	public boolean containsEntity(String id);
	
	public void addEntityListener(EntityListener<E> listener);
	
	public void removeEntityListener(EntityListener<E> listener);

	public void notifyEntityListenersOfAdditions();
	
	public void notifyEntityListenersOfRemovals();
	
	public void notifyEntityListenersOfChanges();
	
	public void fireEntityAdded(E entity);
	
	public void fireEntityAdded(E entity, EntityProperty property);
	
	public void fireEntityRemoved(E entity);
	
	public void fireEntityChanged(E entity);
	
	public EntityMonitor<E> getMonitor();

	public String createUuid();
	
	public void clearCache();
}
