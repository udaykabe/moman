package net.deuce.moman.command.navigate;

import net.deuce.moman.report.EnvelopeBreakdownReportView;

public class ActivateEnvelopeBreakdownReportView extends AbstractActivateView {

	public static final String ID = "net.deuce.moman.command.navigate.envelopeBreakdownReport";

	@Override
	public String getViewId() {
		return EnvelopeBreakdownReportView.ID;
	}

}