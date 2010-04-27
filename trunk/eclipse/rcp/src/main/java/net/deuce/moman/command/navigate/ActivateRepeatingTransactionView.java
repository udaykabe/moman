package net.deuce.moman.command.navigate;

import net.deuce.moman.transaction.ui.RepeatingTransactionView;

public class ActivateRepeatingTransactionView extends AbstractActivateView {

	public static final String ID = "net.deuce.moman.command.navigate.account";

	public String getViewId() {
		return RepeatingTransactionView.ID;
	}

}
