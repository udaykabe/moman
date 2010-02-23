package net.deuce.moman.command.navigate;

import net.deuce.moman.report.CashFlowReportView;

public class ActivateCashFlowReportView extends AbstractActivateView {

	public static final String ID = "net.deuce.moman.command.navigate.cashFlowReport";

	@Override
	public String getViewId() {
		return CashFlowReportView.ID;
	}

}