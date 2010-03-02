package net.deuce.moman.envelope.ui;

import java.util.Date;

import net.deuce.moman.envelope.model.Envelope;
import net.deuce.moman.ui.DateSelectionEditingSupport;

import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.swt.widgets.Composite;

public class SavingsGoalDateSelectionEditingSupport extends DateSelectionEditingSupport {
	
	public SavingsGoalDateSelectionEditingSupport(ColumnViewer viewer, Composite parent) {
		super(viewer, parent);
	}

	protected void doSetValue(Object element, Object value) {
		Envelope envelope = (Envelope)element;
		envelope.setSavingsGoalDate((Date)value);
	}

	@Override
	protected Object doGetValue(Object element) {
		return ((Envelope)element).getSavingsGoalDate();
	}

}
