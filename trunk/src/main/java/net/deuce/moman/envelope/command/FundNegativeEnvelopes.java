package net.deuce.moman.envelope.command;

import net.deuce.moman.account.model.Account;
import net.deuce.moman.account.service.AccountService;
import net.deuce.moman.account.ui.SelectAccountDialog;
import net.deuce.moman.envelope.model.EnvelopeFactory;
import net.deuce.moman.envelope.service.EnvelopeService;
import net.deuce.moman.service.ServiceContainer;
import net.deuce.moman.service.ServiceNeeder;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;

public class FundNegativeEnvelopes extends AbstractHandler {

	public static final String ID = "net.deuce.moman.envelope.command.fundNegative";
	
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		
		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindow(event);
		AccountService accountService = ServiceNeeder.instance().getAccountService();
		Account account = null;
		if (accountService.getEntities().size() == 1) {
			account = accountService.getEntities().get(0);
		} else {
			SelectAccountDialog dialog = new SelectAccountDialog(window.getShell());
			dialog.create();
			if (dialog.open() == Window.OK) {
				account = dialog.getEntity();
			}
		}
		
		if (account != null) {
			try {
				final Account targetAccount = account;
				if (MessageDialog.openQuestion(window.getShell(), "Fund Negative Envelopes?",
						"Are you sure you want attempt to fund the negative envelopes from Available?")) {
					BusyIndicator.showWhile(Display.getCurrent(), new Runnable() {
						@Override
		                public void run() {
							ServiceContainer serviceContainer = ServiceNeeder.instance().getServiceContainer();
							serviceContainer.startQueuingNotifications();
							try {
								EnvelopeService envelopeService = ServiceNeeder.instance().getEnvelopeService();
								EnvelopeFactory envelopeFactory = ServiceNeeder.instance().getEnvelopeFactory();
								envelopeService.distributeToNegativeEnvelopes(targetAccount, envelopeFactory.createTopLevelEnvelope(), envelopeService.getAvailableEnvelope().getBalance());
							} finally {
								serviceContainer.stopQueuingNotifications();
							}
						}
					});
				}
			} catch (Exception e) {
				e.printStackTrace();
				throw new ExecutionException(e.getMessage(), e);
			}
		}
		
		return null;
	}

}