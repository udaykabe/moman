package net.deuce.moman.command.navigate;

import net.deuce.moman.envelope.ui.BillView;

public class ActivateBillView extends AbstractActivateView {

	public static final String ID = "net.deuce.moman.command.navigate.bill";

	public String getViewId() {
		return BillView.ID;
	}

}
