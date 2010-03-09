package net.deuce.moman.account.command;

import java.util.List;

import net.deuce.moman.account.model.Account;
import net.deuce.moman.account.ui.AccountDialog;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;

public class Edit extends AbstractAccountHandler {
	
	public static final String ID = "net.deuce.moman.account.command.edit";
	
	public Edit() {
		super(false);
	}

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindow(event);
		List<Account> accounts = getAccounts(window);
		
		if (accounts.size() == 1) {
			AccountDialog dialog = new AccountDialog(window.getShell());
			dialog.setAccount(accounts.get(0));
			dialog.create();
			dialog.open();
		}
		return null;
	}

}
