package net.deuce.moman.account.command;

import java.util.List;

import net.deuce.moman.account.ui.AccountDialog;
import net.deuce.moman.entity.ServiceProvider;
import net.deuce.moman.entity.model.account.Account;
import net.deuce.moman.entity.service.ServiceManager;
import net.deuce.moman.entity.service.account.AccountService;
import net.deuce.moman.entity.service.envelope.EnvelopeService;

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

	private AccountService accountService = ServiceProvider.instance().getAccountService();

	private EnvelopeService envelopeService = ServiceProvider.instance().getEnvelopeService();

	private ServiceManager serviceManager = ServiceProvider.instance().getServiceManager();

	public Object execute(ExecutionEvent event) throws ExecutionException {
		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindow(event);

		AccountDialog dialog = new AccountDialog(window.getShell(), false);
		dialog.create();
		if (dialog.open() == Window.OK) {
			if (accountService.doesAccountExist(dialog.getAccount())) {
				String message = "An account already exists with the same routing and account numbers. Proceed?";
				MessageDialog messageDialog = new MessageDialog(window
						.getShell(), "Duplicate account?", null, message,
						MessageDialog.QUESTION, new String[] {
								IDialogConstants.YES_LABEL,
								IDialogConstants.NO_LABEL }, 1);
				if (messageDialog.open() != 0) {
					return null;
				}
			}
			Account account = dialog.getAccount();
			account.setSelected(true);
			List<String> ids = serviceManager.startQueuingNotifications();
			try {
				accountService.addEntity(account);
				if (accountService.getEntities().size() == 1) {
					envelopeService.importDefaultEnvelopes();
				}
			} finally {
				serviceManager.stopQueuingNotifications(ids);
			}
		}
		return null;
	}

}
