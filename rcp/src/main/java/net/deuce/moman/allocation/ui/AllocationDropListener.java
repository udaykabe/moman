package net.deuce.moman.allocation.ui;

import java.util.LinkedList;
import java.util.List;

import net.deuce.moman.allocation.model.Allocation;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.ViewerDropAdapter;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.TransferData;

public class AllocationDropListener extends ViewerDropAdapter {
	
	private static final int ALLOCATION_MOVE = DND.DROP_MOVE;

	private Allocation target;
	private int operation = DND.DROP_NONE;
	private int location = LOCATION_NONE;

	public AllocationDropListener(TableViewer viewer) {
		super(viewer);
	}
	
	@Override
	public void drop(DropTargetEvent event) {
		location = determineLocation(event);
		target = (Allocation) determineTarget(event);
		operation = getCurrentOperation();
		
		switch (location) {
		case LOCATION_NONE:
			target = null;
			break;
		}
		super.drop(event);
	}

	@Override
	public boolean performDrop(Object data) {
		if (target != null) {
			
				
			switch (operation) {
			case ALLOCATION_MOVE:
				List<Integer> indexes = new LinkedList<Integer>();
				for (int id : (int[])data) {
					indexes.add(id);
				}

				try {
				target.getAllocationSet().moveAllocations(indexes, target, 
						location == LOCATION_BEFORE || location == LOCATION_ON);
				} catch (Throwable t) {
					t.printStackTrace();
				}
				break;
			}
			return true;
		}
		return false;
	}

	@Override
	public boolean validateDrop(Object target, int operation,
			TransferData transferType) {
		return true;

	}

}
