package net.deuce.moman.envelope.ui;

import java.util.List;

import net.deuce.moman.transaction.model.Split;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

public class SplitContentProvider implements IStructuredContentProvider {
	
	@Override
	public Object[] getElements(Object inputElement) {
		@SuppressWarnings("unchecked")
		List<Split> split = (List<Split>) inputElement;
		return split.toArray();
	}

	@Override
	public void dispose() {
	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
	}


}
