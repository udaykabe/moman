package net.deuce.moman.command.navigate;

import net.deuce.moman.transaction.ui.TransferView;

public class ActivateTransferView extends AbstractActivateView {

	public static final String ID = "net.deuce.moman.command.navigate.account";

	@Override
	public String getViewId() {
		return TransferView.ID;
	}

}