package net.deuce.moman.account.command;

import net.deuce.moman.account.model.Account;
import net.deuce.moman.account.service.AccountService;
import net.deuce.moman.account.ui.AccountDialog;
import net.deuce.moman.service.ServiceNeeder;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;

public class NewDisconnected extends AbstractHandler {
	
	public static final String ID = "net.deuce.moman.account.command.newDisconnected";

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindow(event);
		
		AccountService service = ServiceNeeder.instance().getAccountService();
		AccountDialog dialog = new AccountDialog(window.getShell(), false);
		dialog.create();
		if (dialog.open() == Window.OK) {
			if (service.doesAccountExist(dialog.getAccount())) {
				String message = "An account already exists with the same routing and account numbers. Proceed?";
				MessageDialog messageDialog = new MessageDialog(window.getShell(), "Duplicate account?", null, message,
						MessageDialog.QUESTION, new String[] { IDialogConstants.YES_LABEL,
								IDialogConstants.NO_LABEL }, 1);
				if (messageDialog.open() != 0) {
					return null;
				}
			}
			Account account = dialog.getAccount();
			account.setSelected(true);
			ServiceNeeder.instance().getAccountService().addEntity(account);
		}
		return null;
	}

}
