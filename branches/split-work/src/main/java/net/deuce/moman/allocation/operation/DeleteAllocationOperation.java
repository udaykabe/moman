package net.deuce.moman.allocation.operation;

import java.util.List;

import net.deuce.moman.allocation.model.Allocation;
import net.deuce.moman.allocation.model.AllocationSet;
import net.deuce.moman.operation.CreateEntityOperation;
import net.deuce.moman.service.EntityService;

import org.eclipse.core.runtime.IProgressMonitor;


public class DeleteAllocationOperation extends CreateEntityOperation<Allocation, EntityService<Allocation>> {
	
	private List<Allocation> allocations;
	private AllocationSet allocationSet;

	public DeleteAllocationOperation(List<Allocation> allocations) {
		super(null, null);
		this.allocations = allocations;
		if (allocations.size() > 0) {
			this.allocationSet = allocations.get(0).getAllocationSet();
		}
	}

	@Override
	protected void doExecute(IProgressMonitor monitor) {
		for (Allocation allocation : allocations) {
			allocationSet.removeAllocation(allocation);
		}
	}

	@Override
	protected void doRedo(IProgressMonitor monitor) {
		for (Allocation allocation : allocations) {
			allocationSet.removeAllocation(allocation);
		}
	}

	@Override
	protected void doUndo(IProgressMonitor monitor) {
		for (Allocation allocation : allocations) {
			allocationSet.addAllocation(allocation);
		}
	}

}
