package net.deuce.moman.command.navigate;

import net.deuce.moman.report.SpendingReportView;

public class ActivateSpendingReportView extends AbstractActivateView {

	public static final String ID = "net.deuce.moman.command.navigate.spendingReport";

	public String getViewId() {
		return SpendingReportView.ID;
	}

}
