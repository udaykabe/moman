package net.deuce.moman.entity.service.allocation.impl;

import net.deuce.moman.entity.model.allocation.AllocationSet;
import net.deuce.moman.entity.service.allocation.AllocationSetService;
import net.deuce.moman.entity.service.impl.EntityServiceImpl;

import org.springframework.stereotype.Component;

@Component("allocationSetService")
public class AllocationSetServiceImpl extends EntityServiceImpl<AllocationSet> 
implements AllocationSetService {
	
	public boolean doesNameExist(String name) {
		for (AllocationSet allocationSet : getEntities()) {
			if (allocationSet.getName().equals(name)) return true;
		}
		return false;
	}
}
