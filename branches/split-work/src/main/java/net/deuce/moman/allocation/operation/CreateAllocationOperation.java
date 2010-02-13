package net.deuce.moman.allocation.operation;

import net.deuce.moman.allocation.model.Allocation;
import net.deuce.moman.allocation.model.AllocationSet;
import net.deuce.moman.operation.CreateEntityOperation;
import net.deuce.moman.service.EntityService;

import org.eclipse.core.runtime.IProgressMonitor;


public class CreateAllocationOperation extends CreateEntityOperation<Allocation, EntityService<Allocation>> {
	
	private Allocation allocation;
	private AllocationSet allocationSet;

	public CreateAllocationOperation(AllocationSet allocationSet, Allocation allocation) {
		super(null, null);
		this.allocation = allocation;
		this.allocationSet = allocationSet;
	}

	@Override
	protected void doExecute(IProgressMonitor monitor) {
		allocationSet.addAllocation(allocation);
	}

	@Override
	protected void doRedo(IProgressMonitor monitor) {
		allocationSet.addAllocation(allocation);
	}

	@Override
	protected void doUndo(IProgressMonitor monitor) {
		allocationSet.removeAllocation(allocation);
	}

}
