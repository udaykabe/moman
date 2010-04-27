package net.deuce.moman.envelope.ui;

import net.deuce.moman.entity.model.envelope.Envelope;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;

public class BillViewerComparator extends ViewerComparator {

	public int compare(Viewer viewer, Object e1, Object e2) {
		return Envelope.BILL_COMPARATOR.compare((Envelope) e1, (Envelope) e2);
	}

}
