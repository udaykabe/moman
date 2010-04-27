package net.deuce.moman.allocation.operation;

import net.deuce.moman.entity.model.allocation.Allocation;
import net.deuce.moman.entity.model.allocation.AllocationSet;
import net.deuce.moman.entity.service.EntityService;
import net.deuce.moman.operation.CreateEntityOperation;

import org.eclipse.core.runtime.IProgressMonitor;

public class CreateAllocationOperation extends
		CreateEntityOperation<Allocation, EntityService<Allocation>> {

	private Allocation allocation;
	private AllocationSet allocationSet;

	public CreateAllocationOperation(AllocationSet allocationSet,
			Allocation allocation) {
		super(null, null);
		this.allocation = allocation;
		this.allocationSet = allocationSet;
	}

	protected void doExecute(IProgressMonitor monitor) {
		allocationSet.addAllocation(allocation);
	}

	protected void doRedo(IProgressMonitor monitor) {
		allocationSet.addAllocation(allocation);
	}

	protected void doUndo(IProgressMonitor monitor) {
		allocationSet.removeAllocation(allocation);
	}

}
