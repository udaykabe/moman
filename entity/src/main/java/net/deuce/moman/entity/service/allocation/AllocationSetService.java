package net.deuce.moman.entity.service.allocation;

import net.deuce.moman.entity.model.allocation.AllocationSet;
import net.deuce.moman.entity.service.EntityService;

public interface AllocationSetService extends EntityService<AllocationSet> {
	
	public boolean doesNameExist(String name);
}
