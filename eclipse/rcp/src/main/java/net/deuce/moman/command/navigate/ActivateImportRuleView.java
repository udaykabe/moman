package net.deuce.moman.command.navigate;

import net.deuce.moman.rule.ui.TransactionRuleView;

public class ActivateImportRuleView extends AbstractActivateView {

	public static final String ID = "net.deuce.moman.command.navigate.importRule";

	public String getViewId() {
		return TransactionRuleView.ID;
	}

}
