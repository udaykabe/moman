package net.deuce.moman.command.navigate;

import net.deuce.moman.envelope.ui.EnvelopeView;

public class ActivateEnvelopeView extends AbstractActivateView {

	public static final String ID = "net.deuce.moman.command.navigate.envelope";

	public String getViewId() {
		return EnvelopeView.ID;
	}

}
