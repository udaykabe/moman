package net.deuce.moman.report;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DateRangeCombo;

public class EnvelopeBreakdownReportView extends AbstractReportView {

	public static final String ID = EnvelopeBreakdownReportView.class.getName();

	@Override
	protected Control doCreateChartControl(
			Composite parent, DateRangeCombo combo) {
		return new EnvelopeBreakdownComposite(parent, combo, SWT.NONE);
	}

}
