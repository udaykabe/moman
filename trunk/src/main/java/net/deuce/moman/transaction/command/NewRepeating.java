package net.deuce.moman.transaction.command;

import java.util.Date;
import java.util.List;

import net.deuce.moman.account.command.AbstractAccountHandler;
import net.deuce.moman.account.model.Account;
import net.deuce.moman.envelope.service.EnvelopeService;
import net.deuce.moman.model.Frequency;
import net.deuce.moman.service.ServiceNeeder;
import net.deuce.moman.transaction.model.RepeatingTransaction;
import net.deuce.moman.transaction.service.RepeatingTransactionService;
import net.deuce.moman.transaction.ui.RepeatingTransactionView;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;

public class NewRepeating extends AbstractAccountHandler {

	public static final String ID = "net.deuce.moman.transaction.command.newRepeating";
	
	public NewRepeating() {
		super(false);
	}

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {

		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindow(event);
		List<Account> accounts = getAccounts(window);
		
		if (accounts.size() == 0) {
			MessageDialog.openError(window.getShell(), "Error", "Please select a source account.");
			return null;
		}

		
		try {
			PlatformUI.getWorkbench().getActiveWorkbenchWindow().getPages()[0]
					.showView(RepeatingTransactionView.ID, null, IWorkbenchPage.VIEW_ACTIVATE);
			EnvelopeService envelopeService = ServiceNeeder.instance().getEnvelopeService();
			RepeatingTransactionService repeatingTransactionService = ServiceNeeder.instance().getRepeatingTransactionService();
			Date date = new Date();
			RepeatingTransaction transaction = ServiceNeeder.instance().getRepeatingTransactionFactory().newEntity(
					null, 0.0, null, date, "Set Description", null, null,
					null, null, accounts.get(0), date, date, Frequency.MONTHLY, 1);
			transaction.setEnabled(true);
			transaction.addSplit(envelopeService.getUnassignedEnvelope(), transaction.getAmount());
			repeatingTransactionService.addEntity(transaction);
		} catch (PartInitException e) {
			e.printStackTrace();
		}

		return null;
	}
}