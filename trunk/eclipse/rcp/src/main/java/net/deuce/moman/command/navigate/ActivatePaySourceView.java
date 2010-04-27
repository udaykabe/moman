package net.deuce.moman.command.navigate;

import net.deuce.moman.income.ui.IncomeView;

public class ActivatePaySourceView extends AbstractActivateView {

	public static final String ID = "net.deuce.moman.command.navigate.paySource";

	public String getViewId() {
		return IncomeView.ID;
	}

}
