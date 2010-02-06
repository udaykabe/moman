package net.deuce.moman.model;

import java.util.Random;
import java.util.UUID;

@SuppressWarnings("unchecked")
public class EntityFactory<E extends AbstractEntity> {
	
	private static Random random = new Random(System.currentTimeMillis());

	public E buildEntity(Class<E> clazz, String id) {
		try {
			E entity = clazz.newInstance();
			entity.setId(id);
			return entity;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	public String createUuid() {
		return UUID.nameUUIDFromBytes(((random.nextInt()+"."+System.currentTimeMillis()).getBytes())).toString();
	}
	
	public E newDefaultEntity(Class<E> clazz) {
		return buildEntity(clazz, createUuid());
	}
}
