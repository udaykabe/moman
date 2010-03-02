package net.deuce.moman.transaction.command;

import java.util.Date;

import net.deuce.moman.account.model.Account;
import net.deuce.moman.account.service.AccountService;
import net.deuce.moman.account.ui.SelectAccountDialog;
import net.deuce.moman.envelope.service.EnvelopeService;
import net.deuce.moman.model.Frequency;
import net.deuce.moman.service.ServiceNeeder;
import net.deuce.moman.transaction.model.RepeatingTransaction;
import net.deuce.moman.transaction.service.RepeatingTransactionService;
import net.deuce.moman.transaction.ui.RepeatingTransactionView;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.window.Window;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;

public class NewRepeating extends AbstractHandler {

	public static final String ID = "net.deuce.moman.transaction.command.newRepeating";
	
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
				PlatformUI.getWorkbench().getActiveWorkbenchWindow().getPages()[0]
						.showView(RepeatingTransactionView.ID, null, IWorkbenchPage.VIEW_ACTIVATE);
				EnvelopeService envelopeService = ServiceNeeder.instance().getEnvelopeService();
				RepeatingTransactionService repeatingTransactionService = ServiceNeeder.instance().getRepeatingTransactionService();
				Date date = new Date();
				RepeatingTransaction transaction = ServiceNeeder.instance().getRepeatingTransactionFactory().newEntity(
						null, 0.0, null, date, "Set Description", null, null,
						null, null, account, date, date, Frequency.MONTHLY, 1);
				transaction.setEnabled(true);
				transaction.addSplit(envelopeService.getUnassignedEnvelope(), transaction.getAmount());
				repeatingTransactionService.addEntity(transaction);
			} catch (Exception e) {
				e.printStackTrace();
				throw new ExecutionException(e.getMessage(), e);
			}
		}
		

		return null;
	}
}