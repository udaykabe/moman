package net.deuce.moman.allocation.ui;

import java.util.List;

import net.deuce.moman.allocation.model.AllocationSet;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

public class AllocationSetContentProvider implements IStructuredContentProvider {

	@Override
	public Object[] getElements(Object inputElement) {
		@SuppressWarnings("unchecked")
		List<AllocationSet> allocationSets = (List<AllocationSet>) inputElement;
		return allocationSets.toArray();
	}

	@Override
	public void dispose() {
	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
	}

}
