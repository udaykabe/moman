package net.deuce.moman.allocation.ui;

import java.util.List;

import net.deuce.moman.allocation.model.Allocation;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

public class AllocationContentProvider implements IStructuredContentProvider {

	@Override
	public Object[] getElements(Object inputElement) {
		@SuppressWarnings("unchecked")
		List<Allocation> allocations = (List<Allocation>) inputElement;
		return allocations.toArray();
	}

	@Override
	public void dispose() {
	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
	}

}
