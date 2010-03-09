package net.deuce.moman.account.command;

import java.util.List;

import net.deuce.moman.account.model.Account;
import net.deuce.moman.service.ServiceNeeder;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;

public class Delete extends AbstractAccountHandler {
	
	public static final String ID = "net.deuce.moman.account.command.delete";
	
	public Delete() {
		super(true);
	}
	
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		
		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindow(event);
		final List<Account> accounts = getAccounts(window);		
		
		if (accounts.size() > 0) {
			String msg;
			if (accounts.size() == 1) {
				msg = "'" + accounts.get(0).getNickname() + "' account?";
			} else {
				msg = accounts.size() + " accounts";
			}
			if (MessageDialog.openQuestion(window.getShell(), "Delete Account?",
					"Are you sure you want to delete the " + msg)) {
				
				BusyIndicator.showWhile(window.getShell().getDisplay(), new Runnable() {
					@Override
					public void run() {
						for (Account account : accounts) {
							ServiceNeeder.instance().getAccountService().removeEntity(account);
							ServiceNeeder.instance().getTransactionService().removeAccountTransactions(account);
						}
					}
				});
			}
		}
		return null;
	}

}
