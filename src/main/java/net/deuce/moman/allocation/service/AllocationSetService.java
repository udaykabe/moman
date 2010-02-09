package net.deuce.moman.allocation.service;

import net.deuce.moman.allocation.model.AllocationSet;
import net.deuce.moman.service.EntityService;

import org.eclipse.jface.viewers.StructuredViewer;
import org.springframework.stereotype.Service;

@Service
public class AllocationSetService extends EntityService<AllocationSet> {
	
	private StructuredViewer allocationViewer;
	
	public StructuredViewer getAllocationViewer() {
		return allocationViewer;
	}

	public void setAllocationViewer(StructuredViewer allocationViewer) {
		this.allocationViewer = allocationViewer;
	}

	public boolean doesNameExist(String name) {
		for (AllocationSet allocationSet : getEntities()) {
			if (allocationSet.getName().equals(name)) return true;
		}
		return false;
	}
}
