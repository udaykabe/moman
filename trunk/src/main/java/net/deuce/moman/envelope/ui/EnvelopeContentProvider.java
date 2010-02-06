package net.deuce.moman.envelope.ui;


import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

public class EnvelopeContentProvider extends BaseEnvelopeContentProvider implements ITreeContentProvider {

	@Override
	public Object[] getChildren(Object parentElement) {
		Object[] children = getEnvelope(parentElement).getChildren().toArray();
		return children;
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
