package net.deuce.moman.allocation.operation;

import java.util.List;

import net.deuce.moman.allocation.model.Allocation;
import net.deuce.moman.allocation.ui.AllocationDropAdapter;
import net.deuce.moman.operation.CreateEntityOperation;
import net.deuce.moman.service.EntityService;

import org.eclipse.core.runtime.IProgressMonitor;


public class MoveAllocationOperation extends CreateEntityOperation<Allocation, EntityService<Allocation>> {
	
	private Allocation target;
	private List<Integer> indexes;
	private int location;

	public MoveAllocationOperation(Allocation target, List<Integer> indexes, int location) {
		super(null, null);
		this.target = target;
		this.indexes = indexes;
		this.location = location;
	}

	@Override
	protected void doExecute(IProgressMonitor monitor) {
		target.getAllocationSet().moveAllocations(indexes, target, 
				location == AllocationDropAdapter.LOCATION_BEFORE || location == AllocationDropAdapter.LOCATION_ON);
	}

	@Override
	protected void doRedo(IProgressMonitor monitor) {
		target.getAllocationSet().moveAllocations(indexes, target, 
				location == AllocationDropAdapter.LOCATION_BEFORE || location == AllocationDropAdapter.LOCATION_ON);
	}

	@Override
	protected void doUndo(IProgressMonitor monitor) {
	}

}
