package net.deuce.moman.command.navigate;

import net.deuce.moman.account.ui.AccountView;

public class ActivateAccountView extends AbstractActivateView {

	public static final String ID = "net.deuce.moman.command.navigate.account";

	public String getViewId() {
		return AccountView.ID;
	}

}
