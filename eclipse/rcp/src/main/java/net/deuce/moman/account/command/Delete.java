package net.deuce.moman.account.command;

import java.util.List;

import net.deuce.moman.entity.ServiceProvider;
import net.deuce.moman.entity.model.account.Account;
import net.deuce.moman.entity.service.account.AccountService;
import net.deuce.moman.entity.service.transaction.TransactionService;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;

public class Delete extends AbstractAccountHandler {

	public static final String ID = "net.deuce.moman.account.command.delete";

	private AccountService accountService = ServiceProvider.instance().getAccountService();

	private TransactionService transactionService = ServiceProvider.instance().getTransactionService();

	public Delete() {
		super(true);
	}

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
			if (MessageDialog.openQuestion(window.getShell(),
					"Delete Account?", "Are you sure you want to delete the "
							+ msg)) {

				BusyIndicator.showWhile(window.getShell().getDisplay(),
						new Runnable() {

							public void run() {
								for (Account account : accounts) {
									accountService.removeEntity(account);
									transactionService
											.removeAccountTransactions(account);
								}
							}
						});
			}
		}
		return null;
	}

}
