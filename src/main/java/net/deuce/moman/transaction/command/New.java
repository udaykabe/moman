package net.deuce.moman.transaction.command;

import java.util.Date;
import java.util.List;

import net.deuce.moman.account.command.AbstractAccountHandler;
import net.deuce.moman.account.model.Account;
import net.deuce.moman.envelope.service.EnvelopeService;
import net.deuce.moman.service.ServiceNeeder;
import net.deuce.moman.transaction.model.InternalTransaction;
import net.deuce.moman.transaction.service.TransactionService;
import net.deuce.moman.transaction.ui.RegisterView;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;

public class New extends AbstractAccountHandler {

	public static final String ID = "net.deuce.moman.transaction.command.new";
	
	public New() {
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
					.showView(RegisterView.ID, null, IWorkbenchPage.VIEW_ACTIVATE);
			EnvelopeService envelopeService = ServiceNeeder.instance().getEnvelopeService();
			TransactionService transactionService = ServiceNeeder.instance().getTransactionService();
			InternalTransaction transaction = ServiceNeeder.instance().getTransactionFactory().newEntity(
					null, 0.0, null, new Date(), "Set Description", null, null,
					null, null, accounts.get(0));
			transaction.addSplit(envelopeService.getUnassignedEnvelope());
			transactionService.addEntity(transaction);
		} catch (PartInitException e) {
			e.printStackTrace();
		}

		return null;
	}
}