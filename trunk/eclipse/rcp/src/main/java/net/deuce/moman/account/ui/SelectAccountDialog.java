package net.deuce.moman.account.ui;

import net.deuce.moman.entity.ServiceProvider;
import net.deuce.moman.entity.model.account.Account;
import net.deuce.moman.entity.service.EntityService;
import net.deuce.moman.entity.service.account.AccountService;
import net.deuce.moman.ui.AbstractSelectEntityDialog;
import net.deuce.moman.ui.EntityLabelProvider;

import org.eclipse.swt.widgets.Shell;

public class SelectAccountDialog extends AbstractSelectEntityDialog<Account> {

	private AccountService accountService = ServiceProvider.instance().getAccountService();

	public SelectAccountDialog(Shell shell) {
		super(shell);
	}

	protected EntityService<Account> getService() {
		return accountService;
	}

	protected String getEntityTitle() {
		return "Select an Account:";
	}

	protected EntityLabelProvider<Account> getEntityLabelProvider() {
		return new AccountEntityLabelProvider();
	}

}
