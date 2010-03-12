package net.deuce.moman.entity.model.impl;

import java.util.Random;
import java.util.UUID;

import net.deuce.moman.entity.model.AbstractEntity;
import net.deuce.moman.entity.model.EntityFactory;

@SuppressWarnings("unchecked")
public class EntityFactoryImpl<E extends AbstractEntity> implements EntityFactory<E> {
	
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
