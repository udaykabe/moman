package net.deuce.moman.income.ui;

import net.deuce.moman.entity.model.envelope.Envelope;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;

public class IncomeViewerComparator extends ViewerComparator {

	public int compare(Viewer viewer, Object e1, Object e2) {
		return Envelope.BILL_COMPARATOR.compare((Envelope) e1, (Envelope) e2);
	}

}
