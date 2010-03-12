package net.deuce.moman.report;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DateRangeCombo;

public class SpendingReportView extends AbstractReportView {

	public static final String ID = SpendingReportView.class.getName();

	protected Control doCreateChartControl(Composite parent,
			DateRangeCombo combo) {
		return new SpendingComposite(parent, combo, SWT.NONE);
	}

}
