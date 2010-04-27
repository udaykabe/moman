package net.deuce.moman.envelope.ui;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

public class EnvelopeContentProvider extends BaseEnvelopeContentProvider
		implements ITreeContentProvider {

	public Object[] getChildren(Object parentElement) {
		return getEnvelope(parentElement).getChildren().toArray();
	}

	public Object getParent(Object element) {
		return getEnvelope(element).getParent();
	}

	public boolean hasChildren(Object element) {
		return getEnvelope(element).hasChildren();
	}

	public Object[] getElements(Object inputElement) {
		return getChildren(inputElement);
	}

	public void dispose() {
	}

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
	}

}
