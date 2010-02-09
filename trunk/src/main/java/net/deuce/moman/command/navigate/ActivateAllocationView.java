package net.deuce.moman.command.navigate;

import net.deuce.moman.allocation.ui.AllocationView;

public class ActivateAllocationView extends AbstractActivateView {

	public static final String ID = "net.deuce.moman.command.navigate.allocation";

	@Override
	public String getViewId() {
		return AllocationView.ID;
	}

}