package net.deuce.moman.envelope.command;

import java.util.List;

import net.deuce.moman.account.ui.SelectAccountDialog;
import net.deuce.moman.entity.ServiceProvider;
import net.deuce.moman.entity.model.account.Account;
import net.deuce.moman.entity.model.envelope.EnvelopeFactory;
import net.deuce.moman.entity.service.ServiceManager;
import net.deuce.moman.entity.service.account.AccountService;
import net.deuce.moman.entity.service.envelope.EnvelopeService;

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

	private AccountService accountService = ServiceProvider.instance().getAccountService();

	private EnvelopeService envelopeService = ServiceProvider.instance().getEnvelopeService();

	private EnvelopeFactory envelopeFactory = ServiceProvider.instance().getEnvelopeFactory();

	private ServiceManager serviceManager = ServiceProvider.instance().getServiceManager();

	public Object execute(ExecutionEvent event) throws ExecutionException {

		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindow(event);
		Account account = null;
		if (accountService.getEntities().size() == 1) {
			account = accountService.getEntities().get(0);
		} else {
			SelectAccountDialog dialog = new SelectAccountDialog(window
					.getShell());
			dialog.create();
			if (dialog.open() == Window.OK) {
				account = dialog.getEntity();
			}
		}

		if (account != null) {
			try {
				final Account targetAccount = account;
				if (MessageDialog
						.openQuestion(window.getShell(),
								"Fund Negative Envelopes?",
								"Are you sure you want attempt to fund the negative envelopes from Available?")) {
					BusyIndicator.showWhile(Display.getCurrent(),
							new Runnable() {

								public void run() {
									List<String> ids = serviceManager
											.startQueuingNotifications();
									try {
										envelopeService
												.distributeToNegativeEnvelopes(
														targetAccount,
														envelopeFactory
																.createTopLevelEnvelope(),
														envelopeService
																.getAvailableEnvelope()
																.getBalance());
									} finally {
										serviceManager
												.stopQueuingNotifications(ids);
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
