package net.deuce.moman.envelope.ui;

import java.util.LinkedList;
import java.util.List;

import net.deuce.moman.envelope.model.Envelope;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

public class EnvelopeSelectionContentProvider extends BaseEnvelopeContentProvider implements ITreeContentProvider {
	
	private boolean allowBills = false;
	
	public boolean isAllowBills() {
		return allowBills;
	}

	public void setAllowBills(boolean allowBills) {
		this.allowBills = allowBills;
	}

	@Override
	public Object[] getChildren(Object parentElement) {
		List<Envelope> children = new LinkedList<Envelope>();
		for (Envelope child : getEnvelope(parentElement).getChildren()) {
			if (allowBills || !child.isBill()) {
				children.add(child);
			}
		}
		return children.toArray();
	}

	@Override
	public Object getParent(Object element) {
		return getEnvelope(element).getParent();
	}

	@Override
	public boolean hasChildren(Object element) {
		return getEnvelope(element).hasChildren();
	}

	@Override
	public Object[] getElements(Object inputElement) {
		return getChildren(inputElement);
	}

	@Override
	public void dispose() {
	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
	}

}
