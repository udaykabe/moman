package net.deuce.moman.account.command;

import java.util.List;

import net.deuce.moman.account.model.Account;
import net.deuce.moman.transaction.ui.TransactionImportView;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;

public class Import extends AbstractAccountHandler {
	
	public static final String ID = "net.deuce.moman.account.command.import";
	
	public Import() {
		super(false);
	}
	
	protected boolean force() {
		return false;
	}

	@Override
	public Object execute(final ExecutionEvent event) throws ExecutionException {
		final IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindow(event);
		
		List<Account> accounts = getAccounts(window);
		
		if (accounts.size() == 0) return null;
		
		try {
			new ImportExecuter(window.getShell(), accounts.get(0), force(), TransactionImportView.ID).execute();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
}
