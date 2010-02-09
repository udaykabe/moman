package net.deuce.moman.command.navigate;

import net.deuce.moman.transaction.ui.TransactionImportView;

public class ActivateImportView extends AbstractActivateView {

	public static final String ID = "net.deuce.moman.command.navigate.import";

	@Override
	public String getViewId() {
		return TransactionImportView.ID;
	}

}