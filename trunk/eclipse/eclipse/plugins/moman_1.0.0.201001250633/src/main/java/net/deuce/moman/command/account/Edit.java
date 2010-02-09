package net.deuce.moman.command.account;

import net.deuce.moman.model.account.Account;
import net.deuce.moman.rcp.account.AccountDialog;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;

public class Edit extends AbstractAccountHandler {
	
	public static final String ID = "net.deuce.moman.command.account.edit";

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindow(event);
		Account account = getAccount(window);
		
		if (account != null) {
			AccountDialog dialog = new AccountDialog(window.getShell());
			dialog.setAccount(account);
			dialog.create();
			dialog.open();
		}
		return null;
	}

}
