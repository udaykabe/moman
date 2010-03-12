package net.deuce.moman.entity.model.allocation.impl;

import net.deuce.moman.entity.model.allocation.AllocationSet;
import net.deuce.moman.entity.model.allocation.AllocationSetFactory;
import net.deuce.moman.entity.model.impl.EntityFactoryImpl;
import net.deuce.moman.entity.model.income.Income;

import org.springframework.stereotype.Component;

@Component("allocationSetFactory")
public class AllocationSetFactoryImpl extends EntityFactoryImpl<AllocationSet>
implements AllocationSetFactory {
	
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
