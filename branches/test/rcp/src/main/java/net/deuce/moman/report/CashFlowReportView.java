package net.deuce.moman.report;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DateRangeCombo;

public class CashFlowReportView extends AbstractReportView {

	public static final String ID = CashFlowReportView.class.getName();

	@Override
	protected AbstractTransactionReportCanvas doCreateChartControl(
			Composite parent, DateRangeCombo combo) {
		return new CashFlowCanvas(parent, combo, SWT.NONE);
	}

}
