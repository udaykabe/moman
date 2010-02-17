package net.deuce.moman.transaction.command;

import java.util.Date;

import net.deuce.moman.account.model.Account;
import net.deuce.moman.account.service.AccountService;
import net.deuce.moman.account.ui.SelectAccountDialog;
import net.deuce.moman.envelope.service.EnvelopeService;
import net.deuce.moman.service.ServiceNeeder;
import net.deuce.moman.transaction.model.InternalTransaction;
import net.deuce.moman.transaction.service.TransactionService;
import net.deuce.moman.transaction.ui.RegisterView;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.window.Window;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;

public class New extends AbstractHandler {

	public static final String ID = "net.deuce.moman.transaction.command.new";
	
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
						.showView(RegisterView.ID, null, IWorkbenchPage.VIEW_ACTIVATE);
				EnvelopeService envelopeService = ServiceNeeder.instance().getEnvelopeService();
				TransactionService transactionService = ServiceNeeder.instance().getTransactionService();
				InternalTransaction transaction = ServiceNeeder.instance().getTransactionFactory().newEntity(
						null, 0.0, null, new Date(), "Set Description", null, null,
						null, null, account);
				transaction.addSplit(envelopeService.getUnassignedEnvelope(), transaction.getAmount());
				envelopeService.clearSelectedEnvelope();
				transactionService.addEntity(transaction);
			} catch (Exception e) {
				e.printStackTrace();
				throw new ExecutionException(e.getMessage(), e);
			}
		}
		
		return null;
	}
}