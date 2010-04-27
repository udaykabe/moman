package net.deuce.moman.entity.model;


@SuppressWarnings("unchecked")
public interface EntityFactory<E extends AbstractEntity> {
	
	public E buildEntity(Class<E> clazz, String id);
	
	public String createUuid();
	
	public E newDefaultEntity(Class<E> clazz);
}
