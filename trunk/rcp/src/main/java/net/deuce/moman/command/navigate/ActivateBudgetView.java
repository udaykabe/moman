package net.deuce.moman.command.navigate;

import net.deuce.moman.envelope.ui.BudgetView;

public class ActivateBudgetView extends AbstractActivateView {

	public static final String ID = "net.deuce.moman.command.navigate.budget";

	public String getViewId() {
		return BudgetView.ID;
	}

}
