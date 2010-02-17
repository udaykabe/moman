package net.deuce.moman.account.ui;

import net.deuce.moman.account.model.Account;
import net.deuce.moman.service.ServiceNeeder;
import net.deuce.moman.ui.AbstractSelectEntityDialog;
import net.deuce.moman.ui.EntityLabelProvider;

import org.eclipse.swt.widgets.Shell;

public class SelectAccountDialog extends AbstractSelectEntityDialog<Account> {
	
	public SelectAccountDialog(Shell shell) {
		super(shell, ServiceNeeder.instance().getAccountService());
	}

	@Override
	protected String getEntityTitle() {
		return "Select an Account:";
	}
	
	@Override
	protected EntityLabelProvider<Account> getEntityLabelProvider() {
		return new AccountEntityLabelProvider();
	}

}
