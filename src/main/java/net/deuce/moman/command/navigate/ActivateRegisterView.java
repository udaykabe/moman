package net.deuce.moman.command.navigate;

import net.deuce.moman.transaction.ui.RegisterView;

public class ActivateRegisterView extends AbstractActivateView {

	public static final String ID = "net.deuce.moman.command.navigate.register";

	@Override
	public String getViewId() {
		return RegisterView.ID;
	}

}