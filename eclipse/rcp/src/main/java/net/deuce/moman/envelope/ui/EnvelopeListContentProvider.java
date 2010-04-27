package net.deuce.moman.envelope.ui;

import java.util.List;

import net.deuce.moman.entity.model.envelope.Envelope;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

public class EnvelopeListContentProvider implements IStructuredContentProvider {

	public Object[] getElements(Object inputElement) {
		@SuppressWarnings("unchecked")
		List<Envelope> bills = (List<Envelope>) inputElement;
		return bills.toArray();
	}

	public void dispose() {
		// TODO Auto-generated method stub

	}

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		// TODO Auto-generated method stub

	}

}
