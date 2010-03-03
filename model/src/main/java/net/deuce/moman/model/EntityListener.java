package net.deuce.moman.model;


@SuppressWarnings("unchecked")
public interface EntityListener<E extends AbstractEntity>  {
	
	public void entityAdded(EntityEvent<E> event);
	public void entityRemoved(EntityEvent<E> event);
	public void entityChanged(EntityEvent<E> event);
}
