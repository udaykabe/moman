package net.deuce.moman.allocation.model;

import net.deuce.moman.income.model.Income;
import net.deuce.moman.model.EntityFactory;

import org.springframework.stereotype.Component;

@Component
public class AllocationSetFactory extends EntityFactory<AllocationSet> {
	
	public AllocationSet buildEntity(String id, String name, Income income) {
		AllocationSet entity = super.buildEntity(AllocationSet.class, id);
		entity.setName(name);
		entity.setIncome(income);
		return entity;
	}
	
	public AllocationSet newEntity(String name, Income income) {
		return buildEntity(createUuid(), name, income);
	}
	
}
