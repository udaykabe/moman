package net.deuce.moman.model;


public interface EntityListener<E extends MomanEntity>  {
	
	public void entityAdded(EntityEvent<E> event);
	public void entityRemoved(EntityEvent<E> event);
	public void entityChanged(EntityEvent<E> event);
}
