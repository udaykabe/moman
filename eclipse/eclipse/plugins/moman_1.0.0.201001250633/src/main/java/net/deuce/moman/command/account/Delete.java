package net.deuce.moman.command.account;

import net.deuce.moman.model.Registry;
import net.deuce.moman.model.account.Account;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;

public class Delete extends AbstractAccountHandler {
	
	public static final String ID = "net.deuce.moman.command.account.delete";

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		
		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindow(event);
		Account account = getAccount(window);		
		
		if (account != null) {
			if (MessageDialog.openQuestion(window.getShell(), "Delete Account?",
					"Are you sure you want to delete the '" + account.getNickname() + "' account?")) {
				Registry.instance().removeAccount(account);
			}
		}
		return null;
	}

}
