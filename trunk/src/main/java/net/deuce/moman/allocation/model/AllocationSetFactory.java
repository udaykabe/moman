package net.deuce.moman.allocation.model;

import net.deuce.moman.model.EntityFactory;

import org.springframework.stereotype.Component;

@Component
public class AllocationSetFactory extends EntityFactory<AllocationSet> {
	
	public AllocationSet buildEntity(String id, String name) {
		AllocationSet entity = super.buildEntity(AllocationSet.class, id);
		entity.setName(name);
		return entity;
	}
	
	public AllocationSet newEntity(String name) {
		return buildEntity(createUuid(), name);
	}
	
}
