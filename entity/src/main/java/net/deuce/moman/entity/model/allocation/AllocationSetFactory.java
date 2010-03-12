package net.deuce.moman.entity.model.allocation;

import net.deuce.moman.entity.model.EntityFactory;
import net.deuce.moman.entity.model.income.Income;

public interface AllocationSetFactory extends EntityFactory<AllocationSet> {
	
	public AllocationSet buildEntity(String id, String name, Income income);
	public AllocationSet newEntity(String name, Income income);
}
