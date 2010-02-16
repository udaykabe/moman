package net.deuce.moman.envelope.command;

import java.util.List;

import net.deuce.moman.account.command.AbstractAccountHandler;
import net.deuce.moman.account.model.Account;
import net.deuce.moman.envelope.model.EnvelopeFactory;
import net.deuce.moman.envelope.service.EnvelopeService;
import net.deuce.moman.service.ServiceContainer;
import net.deuce.moman.service.ServiceNeeder;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;

public class FundNegativeEnvelopes extends AbstractAccountHandler {

	public static final String ID = "net.deuce.moman.envelope.command.fundNegative";
	
	public FundNegativeEnvelopes() {
		super(false);
	}

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindow(event);
		final List<Account> accounts = getAccounts(window);
		
		if (accounts != null && accounts.size() == 1) {
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
							envelopeService.distributeToNegativeEnvelopes(accounts.get(0), envelopeFactory.createTopLevelEnvelope(), envelopeService.getAvailableEnvelope().getBalance());
						} finally {
							serviceContainer.stopQueuingNotifications();
						}
					}
				});
			}
		}
		
		return null;
	}

}