package net.deuce.moman.envelope.command;

import net.deuce.moman.entity.ServiceProvider;
import net.deuce.moman.entity.model.account.Account;
import net.deuce.moman.entity.model.envelope.Envelope;
import net.deuce.moman.entity.service.account.AccountService;
import net.deuce.moman.entity.service.envelope.EnvelopeService;
import net.deuce.moman.envelope.ui.EnvelopeTransferDialog;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;

public class Transfer extends AbstractEnvelopeHandler {

	public static final String ID = "net.deuce.moman.envelope.command.new";

	private AccountService accountService = ServiceProvider.instance().getAccountService();

	private EnvelopeService envelopeService = ServiceProvider.instance().getEnvelopeService();

	public Object execute(ExecutionEvent event) throws ExecutionException {
		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindow(event);
		Envelope envelope = getEnvelope(window);

		if (envelope == null) {
			envelope = envelopeService.getAvailableEnvelope();
		}

		Account account = null;
		if (accountService.getSelectedAccounts().size() > 0) {
			account = accountService.getSelectedAccounts().get(0);
		}

		EnvelopeTransferDialog dialog = new EnvelopeTransferDialog(Display
				.getCurrent().getActiveShell(), account, account, null, null);
		try {
			dialog.create();
			dialog.open();
		} catch (Throwable t) {
			t.printStackTrace();
		}
		return null;
	}

}
