package net.deuce.moman.envelope.command;

import net.deuce.moman.account.model.Account;
import net.deuce.moman.account.service.AccountService;
import net.deuce.moman.envelope.model.Envelope;
import net.deuce.moman.envelope.ui.EnvelopeTransferDialog;
import net.deuce.moman.service.ServiceNeeder;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;

public class Transfer extends AbstractEnvelopeHandler {

	public static final String ID = "net.deuce.moman.envelope.command.new";

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindow(event);
		Envelope envelope = getEnvelope(window);
		
		if (envelope == null) {
			envelope = ServiceNeeder.instance().getEnvelopeService().getAvailableEnvelope();
		}
		
		AccountService accountService = ServiceNeeder.instance().getAccountService();
		Account account = null;
		if (accountService.getSelectedAccounts().size() > 0) {
			account = accountService.getSelectedAccounts().get(0);
		}
		
		EnvelopeTransferDialog dialog = new EnvelopeTransferDialog(
				Display.getCurrent().getActiveShell(), account,
				account, null, null);
		try {
		dialog.create();
		dialog.open();
		} catch (Throwable t) {
			t.printStackTrace();
		}
		return null;
	}

}