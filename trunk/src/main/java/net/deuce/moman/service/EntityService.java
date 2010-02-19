package net.deuce.moman.service;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import net.deuce.moman.model.AbstractEntity;
import net.deuce.moman.model.EntityListener;
import net.deuce.moman.model.EntityMonitor;

import org.eclipse.jface.viewers.StructuredViewer;

@SuppressWarnings("unchecked")
public class EntityService<E extends AbstractEntity> {

	private static Random random = new Random(System.currentTimeMillis());
	private Map<String, E> entities = new HashMap<String, E>();
	private EntityMonitor<E> monitor = new EntityMonitor<E>();
	private StructuredViewer viewer;
	private E addedEntity;
	
	public boolean isSingleChange() {
		return monitor.isSingleChange();
	}

	public void setSingleChange(boolean singleChange) {
		monitor.setSingleChange(singleChange);
	}
	
	public boolean isQueuingNotifications() {
		return monitor.isQueuingNotifications();
	}
	
	public void startQueuingNotifications() {
		monitor.setQueuingNotifications(true);
	}
	
	public void stopQueuingNotifications() {
		monitor.setQueuingNotifications(false);
	}
	
	public StructuredViewer getViewer() {
		return viewer;
	}

	public void setViewer(StructuredViewer viewer) {
		this.viewer = viewer;
	}
	
	public void updateViewers(List<E> list) {
		for (E entity : list) {
			viewer.refresh(entity);
		}
	}
	
	public E getAddedEntity() {
		return addedEntity;
	}

	public void setAddedEntity(E addedEntity) {
		this.addedEntity = addedEntity;
	}

	public LinkedList<E> getEntities() {
		return new LinkedList<E>(entities.values());
	}
	
	public boolean isClearable() {
		return true;
	}
	
	public List<E> getOrderedEntities(boolean reverse) {
		List<E> list = getEntities();
		if (list.size() > 0) {
			E entity = list.get(0);
			Comparator<E> comparator = reverse ? entity.getReverseComparator() :
				entity.getForwardComparator();
			Collections.sort(list, comparator);
		}
		return list;
	}
	
	public boolean entityExists(String id) {
		return findEntity(id) != null;
	}
	
	public E findEntity(String id) {
		return entities.get(id);
	}
	
	public E getEntity(String id) {
		E entity = entities.get(id);
		if (entity == null) {
			throw new RuntimeException("No entity exists with ID " + id);
		}
		return entity;
	}
	
	public void addEntity(E entity) {
		addEntity(entity, true);
	}
	
	public void addEntity(E entity, boolean notify) {
		if (entity.getId() == null) {
			throw new RuntimeException("No uuid set");
		}
		
		if (entity.getId() != null && entities.containsKey(entity.getId())) {
			throw new RuntimeException("Duplicate entity uuid: " + entity.getId());
		}
		
		addedEntity = entity;
		entities.put(entity.getId(), entity);
		if (notify) {
			monitor.fireEntityAdded(entity);
		}
		entity.setMonitor(monitor);
	}
	
	public void removeEntity(E entity) {
		entities.remove(entity.getId());
		monitor.fireEntityRemoved(entity);
		entity.setMonitor(null);
	}
	
	public void setEntities(List<E> entities) {
		this.entities.clear();
		if (entities != null) {
			for (E entitiy : entities) {
				if (entitiy.getId() == null) {
					throw new RuntimeException("No uuid set");
				}
				this.entities.put(entitiy.getId(), entitiy);
			}
			monitor.fireEntityAdded(null);
		} else {
			monitor.fireEntityRemoved(null);
		}
	}
	
	public void clearEntities() {
		if (entities.size() > 0) {
			entities.clear();
			monitor.fireEntityRemoved(null);
		}
	}
	
	public boolean containsEntity(String id) {
		return entities.containsKey(id);
	}
	
	public void addEntityListener(EntityListener<E> listener) {
		monitor.addListener(listener);
	}
	
	public void removeEntityListener(EntityListener<E> listener) {
		monitor.removeListener(listener);
	}

	public void notifyEntityListenersOfAdditions() {
		monitor.fireEntityAdded(null);
	}
	
	public void notifyEntityListenersOfRemovals() {
		monitor.fireEntityRemoved(null);
	}
	
	public void notifyEntityListenersOfChanges() {
		monitor.fireEntityChanged(null);
	}
	
	public void fireEntityAdded(E entity) {
		monitor.fireEntityAdded(entity);
	}
	
	public void fireEntityRemoved(E entity) {
		monitor.fireEntityRemoved(entity);
	}
	
	public void fireEntityChanged(E entity) {
		monitor.fireEntityChanged(entity);
	}
	
	public EntityMonitor<E> getMonitor() {
		return monitor;
	}

	public String createUuid() {
		return UUID.nameUUIDFromBytes(((random.nextInt()+"."+System.currentTimeMillis()).getBytes())).toString();
	}
	
	protected void clearCache() {
		entities.clear();
		notifyEntityListenersOfRemovals();
	}
}
