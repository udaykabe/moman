package net.deuce.moman.rcp.envelope;

import net.deuce.moman.model.envelope.Envelope;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;

public class EnvelopeViewerComparator extends ViewerComparator {

	@Override
	public int compare(Viewer viewer, Object e1, Object e2) {
		return Envelope.CHILD_COMPARATOR.compare((Envelope)e1, (Envelope)e2);
	}

}
