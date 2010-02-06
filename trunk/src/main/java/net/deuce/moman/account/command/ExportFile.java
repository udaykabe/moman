package net.deuce.moman.account.command;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

public class ExportFile extends AbstractAccountHandler {
	
	public static final String ID = "net.deuce.moman.account.command.exportFile";
	
	public ExportFile() {
		super(false);
	}

	@Override
	public Object execute(final ExecutionEvent event) throws ExecutionException {
		/*
		final IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindow(event);
		
		List<Account> accounts = getAccounts(window);
		
		if (accounts.size() == 0) return null;
		
		try {
			new ImportExecuter(window.getShell(), accounts.get(0), false).execute();
		} catch (Exception e) {
			e.printStackTrace();
		}
		*/
		
		return null;
	}
	
}
